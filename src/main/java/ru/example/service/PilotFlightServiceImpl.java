package ru.example.service;

import lombok.extern.slf4j.Slf4j;
import ru.example.model.*;
import ru.example.validator.FlightValidator;
import ru.example.validator.PilotValidator;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class PilotFlightServiceImpl implements PilotFlightService {

    private final Map<Pilot, List<Flight>> flightsByPilot = new HashMap<>();
    private final FlightValidator flightValidator = new FlightValidator();
    private final PilotValidator pilotValidator = new PilotValidator();

    public OutputPilotsAndFlights process(InputPilotsAndFlights inputPilotsAndFlights) {
        OutputPilotsAndFlights outputPilotsAndFlights = new OutputPilotsAndFlights();

        // Распределение в маппу
        savedPilotAndFlights(inputPilotsAndFlights);

        // сохранение в класс Specialist
        savedOutPut(outputPilotsAndFlights);

        return outputPilotsAndFlights;
    }

    public void savedPilotAndFlights(InputPilotsAndFlights inputPilotsAndFlights) {
        List<String> validationErrors = new ArrayList<>();
        inputPilotsAndFlights.getPilots().forEach(pilot -> {
            try {
                log.info("Валидация пилота с ID: {}", pilot.getIdPilot());
                pilotValidator.validate(pilot);
                log.info("Пилот с ID {} прошёл валидацию", pilot.getIdPilot());
            } catch (IllegalArgumentException exception) {
                validationErrors.add("Ошибка валидации ID пилота " + pilot.getIdPilot() + ": " + exception.getMessage());
            }
        });

        Map<Long, Pilot> pilotById = inputPilotsAndFlights.getPilots().stream()
                .collect(Collectors.toMap(Pilot::getIdPilot, pilot -> pilot));

        inputPilotsAndFlights.getFlights().forEach(flight -> {
            boolean isValidFlight = true;
            try {
                log.info("Валидация рейса с ID: {}", flight.getId());
                flightValidator.validate(flight); // Проверка валидности рейса
                log.info("Рейс с ID {} прошёл валидацию", flight.getId());
            } catch (IllegalArgumentException e) {
                // Логируем ошибку валидации рейса
                log.error("Ошибка валидации рейса с ID {}: {}", flight.getId(), e.getMessage());
                validationErrors.add("Ошибка валидации рейса " + flight.getId() + ": " + e.getMessage());
                isValidFlight = false;
            }
            // Связываем пилотов с рейсами, если рейс валидный
            if (isValidFlight) {
                flight.getIdPilots().forEach(pilotId -> {
                    Pilot pilot = pilotById.get(pilotId);
                    if (pilot != null) {
                        flightsByPilot.computeIfAbsent(pilot, k -> new ArrayList<>()).add(flight);
                        log.info("Рейс с ID {} добавлен пилоту с ID {}", flight.getId(), pilot.getIdPilot());
                    }
                });
            }
        });
        // Логируем все ошибки валидации пилотов в консоль
        if (!validationErrors.isEmpty()) {
            System.err.println("Обнаружены ошибки валидации:");
            validationErrors.forEach(System.err::println);
        }
    }

    public void savedOutPut(OutputPilotsAndFlights outputPilotsAndFlights) {
        for (Map.Entry<Pilot, List<Flight>> entry : flightsByPilot.entrySet()) {
            // конкретный пилот
            Pilot pilot = entry.getKey();
            // полёты конкретного пилота
            List<Flight> flights = entry.getValue();

            // Рассчитываем данные по налету
            List<TimeMonth> timeMonths = calculateFlightTime(flights);

            pilot.setTimeMonthList(timeMonths);
            outputPilotsAndFlights.getSpecialists().add(pilot);
        }
    }

    //TODO original
    // Расчёта общего времени полёта за месяц с флагами
    private List<TimeMonth> calculateFlightTime(List<Flight> flights) {
        List<TimeMonth> timeMonths = new ArrayList<>();
        // 1. Рассчитываем налет за день
        Map<LocalDate, Long> hoursPerDay = getHoursPerDay(flights);

        // 2. Рассчитываем налет за неделю
        Map<LocalDate, Long> hoursPerWeek = getHoursPerWeek(hoursPerDay);

        // 3. Рассчитываем налет за месяц
        Map<LocalDate, Long> hoursPerMonth = getHoursPerMonths(hoursPerDay);

        // 4. Рассчитываем количество полетов за месяц
        Map<LocalDate, Long> totalFlightsPerMonth = flights.stream()
                .collect(Collectors.groupingBy(flight -> flight.getDepartureTime().toLocalDate().withDayOfMonth(1),
                        Collectors.counting()));

        for (Map.Entry<LocalDate, Long> monthEntry : hoursPerMonth.entrySet()) {
            LocalDate month = monthEntry.getKey();
            Long totalMonthHours = monthEntry.getValue();

            // Количество полётов за месяц
            Long totalFlightsInMonth = totalFlightsPerMonth.getOrDefault(month, 0L);

            // Флаг превышения месячного лимита
            boolean exceedsMonthlyLimit = totalMonthHours > 80;

            // Создаем объект TimeMonth
            TimeMonth timeMonth = new TimeMonth();
            timeMonth.setDate(month);
            timeMonth.setTotalFlightsInMonth(totalFlightsInMonth);
            timeMonth.setTotalFlightHours(totalMonthHours);
            timeMonth.setExceedsMonthlyLimit(exceedsMonthlyLimit);

            // 5. Проверяем недельный лимит (в рамках этого месяца)
            long maxWeeklyHours = hoursPerWeek.entrySet().stream()
                    .filter(entry -> {
                        LocalDate currentWeekDate = entry.getKey();
                        // Получаем неделю
                        WeekFields weekFields = WeekFields.of(Locale.getDefault());
                        int weekOfYear = currentWeekDate.get(weekFields.weekOfYear());
                        int monthWeekOfYear = month.get(weekFields.weekOfYear());

                        // Если неделя попадает в тот же месяц или захватывает дни из текущего месяца
                        return weekOfYear == monthWeekOfYear || currentWeekDate.getMonth() == month.getMonth();
                    })
                    .mapToLong(Map.Entry::getValue)
                    .max()
                    .orElse(0);

            timeMonth.setExceedsWeeklyLimit(maxWeeklyHours > 36);

            // 6. Проверяем дневной лимит (в рамках этого месяца)
            long maxDayHoursForMonth = hoursPerDay.entrySet().stream()
                    .filter(entry -> entry.getKey().getMonth().equals(month.getMonth()))
                    .mapToLong(Map.Entry::getValue)
                    .max()
                    .orElse(0);
            timeMonth.setExceedsDailyLimit(maxDayHoursForMonth > 8);

            timeMonths.add(timeMonth);
        }
        return timeMonths;
    }

    //TODO original
    // Возвращает количество часов полёта за 1 день
    private Map<LocalDate, Long> getHoursPerDay(List<Flight> flights) {
        // 1 день
        Map<LocalDate, Long> sumHoursPerDay = new TreeMap<>();
        for (Flight flight : flights) {
            // Дата вылета
            LocalDateTime departureTime = flight.getDepartureTime();
            // Дата прилёта
            LocalDateTime arrivalTime = flight.getArrivalTime();

            try {
                flightValidator.validate(flight);
            } catch (IllegalArgumentException e) {
                log.warn("Рейс {} пропущен: {}", flight.getId(), e.getMessage());
                continue; // Пропускаем этот рейс
            }

            while (departureTime.isBefore(arrivalTime)) {
                // День вылета
                LocalDate departureDay = departureTime.toLocalDate();
                // День прилёта
                LocalDate arrivalDay = arrivalTime.toLocalDate();
                // След. день от дня вылета
                LocalDateTime nextDay = departureDay.plusDays(1).atStartOfDay();

                if (departureDay.equals(arrivalDay)) {
                    long durationInHoursDay = Duration.between(departureTime, arrivalTime).toHours();
                    sumHoursPerDay.merge(departureDay, durationInHoursDay, Long::sum);
                    departureTime = nextDay;
                } else {
                    long durationInHours = Duration.between(departureTime, nextDay).toHours();
                    sumHoursPerDay.merge(departureDay, durationInHours, Long::sum);
                    // Обновляем начало на первый день следующего месяца
                    departureTime = nextDay;
                }
            }
        }
        return sumHoursPerDay;
    }

    //TODO original
    // Возвращает количество часов полёта за 7 дней
    private Map<LocalDate, Long> getHoursPerWeek(Map<LocalDate, Long> hoursPerDay) {
        Map<LocalDate, Long> sumHoursPerWeek = new TreeMap<>();

        for (Map.Entry<LocalDate, Long> hoursPerWeek : hoursPerDay.entrySet()) {
            // конкретная день
            LocalDate weekday = hoursPerWeek.getKey();

            // кол-во часов полёта в день
            Long hours = hoursPerWeek.getValue();

            // начало недели исходя из дня
            LocalDate startOfWeek = weekday.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

            // конец недели исходя из дня
            //           LocalDate endOfWeek = weekday.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

            sumHoursPerWeek.merge(startOfWeek, hours, Long::sum);
        }
        return sumHoursPerWeek;
    }

    //TODO original
    // Возвращает количество часов полёта за месяц
    private Map<LocalDate, Long> getHoursPerMonths(Map<LocalDate, Long> hoursPerDay) {
        Map<LocalDate, Long> sumHoursPerMonth = new TreeMap<>();

        for (Map.Entry<LocalDate, Long> hoursPerMonth : hoursPerDay.entrySet()) {
            LocalDate month = hoursPerMonth.getKey();
            Long hours = hoursPerMonth.getValue();
            LocalDate startOfMonth = month.with(TemporalAdjusters.firstDayOfMonth());
            sumHoursPerMonth.merge(startOfMonth, hours, Long::sum);
        }
        return sumHoursPerMonth;
    }
}