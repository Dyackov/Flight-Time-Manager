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
        log.info("Обработка данных InputPilotsAndFlights");
        OutputPilotsAndFlights outputPilotsAndFlights = new OutputPilotsAndFlights();

        try {
            log.debug("Начало сохранения данных пилотов и рейсов...");
            savedPilotAndFlights(inputPilotsAndFlights);
            log.info("Данные пилотов и рейсов успешно сохранены.");

            log.debug("Начало сохранения данных в Specialist...");
            savedOutPut(outputPilotsAndFlights);
            log.info("Данные в Specialist успешно сохранены.");

        } catch (Exception e) {
            log.error("Ошибка при обработке InputPilotsAndFlights", e);
        }
        log.info("Обработка данных InputPilotsAndFlights завершена");
        return outputPilotsAndFlights;
    }

    public void savedOutPut(OutputPilotsAndFlights outputPilotsAndFlights) {
        log.info("Начало обработки данных для сохранения в outputPilotsAndFlights");
        for (Map.Entry<Pilot, List<Flight>> entry : flightsByPilot.entrySet()) {
            Pilot pilot = entry.getKey();
            List<Flight> flights = entry.getValue();

            // Рассчитываем данные по налету
            List<TimeMonth> timeMonths = calculateFlightTime(flights);
            pilot.setTimeMonthList(timeMonths);
            outputPilotsAndFlights.getSpecialists().add(pilot);
        }
        log.info("Завершена обработка данных и сохранение в outputPilotsAndFlights");
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
        if (!validationErrors.isEmpty()) {
            System.err.println("Обнаружены ошибки валидации:");
            validationErrors.forEach(System.err::println);
        }
    }

    //TODO original
    // Расчёта общего времени полёта за месяц с флагами
    private List<TimeMonth> calculateFlightTime(List<Flight> flights) {
        List<TimeMonth> timeMonths = new ArrayList<>();
        log.info("Начало расчета общего времени полетов за месяц");

        Map<LocalDate, Long> hoursPerDay = getHoursPerDay(flights);
        log.debug("Налет по дням: {}", hoursPerDay);

        Map<LocalDate, Long> hoursPerWeek = getHoursPerWeek(hoursPerDay);
        log.debug("Налет по неделям: {}", hoursPerWeek);

        Map<LocalDate, Long> hoursPerMonth = getHoursPerMonths(hoursPerDay);
        log.debug("Налет по месяцам: {}", hoursPerMonth);

        Map<LocalDate, Long> totalFlightsPerMonth = flights.stream()
                .collect(Collectors.groupingBy(flight -> flight.getDepartureTime().toLocalDate().withDayOfMonth(1),
                        Collectors.counting()));
        log.debug("Количество полетов по месяцам: {}", totalFlightsPerMonth);

        for (Map.Entry<LocalDate, Long> monthEntry : hoursPerMonth.entrySet()) {
            LocalDate month = monthEntry.getKey();
            Long totalMonthHours = monthEntry.getValue();

            // Количество полётов за месяц
            Long totalFlightsInMonth = totalFlightsPerMonth.getOrDefault(month, 0L);

            // Флаг превышения месячного лимита
            boolean exceedsMonthlyLimit = totalMonthHours > 80;

            TimeMonth timeMonth = new TimeMonth();
            timeMonth.setDate(month);
            timeMonth.setTotalFlightsInMonth(totalFlightsInMonth);
            timeMonth.setTotalFlightHours(totalMonthHours);
            timeMonth.setExceedsMonthlyLimit(exceedsMonthlyLimit);

            log.debug("Время полетов за месяц {}: {} часов, количество полетов: {}, превышен месячный лимит: {}",
                    month, totalMonthHours, totalFlightsInMonth, exceedsMonthlyLimit);

            // Проверяем недельный лимит (в рамках этого месяца)
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
            log.debug("Максимальное количество часов в неделю для месяца {}: {} часов, превышен недельный лимит: {}",
                    month, maxWeeklyHours, maxWeeklyHours > 36);

            // Проверяем дневной лимит (в рамках этого месяца)
            long maxDayHoursForMonth = hoursPerDay.entrySet().stream()
                    .filter(entry -> entry.getKey().getMonth().equals(month.getMonth()))
                    .mapToLong(Map.Entry::getValue)
                    .max()
                    .orElse(0);
            timeMonth.setExceedsDailyLimit(maxDayHoursForMonth > 8);
            log.debug("Максимальное количество часов в день для месяца {}: {} часов, превышен дневной лимит: {}",
                    month, maxDayHoursForMonth, maxDayHoursForMonth > 8);

            timeMonths.add(timeMonth);
        }
        log.info("Завершен расчет общего времени полетов за месяц");
        return timeMonths;
    }

    //TODO original
    // Возвращает количество часов полёта за 1 день
    private Map<LocalDate, Long> getHoursPerDay(List<Flight> flights) {
        Map<LocalDate, Long> sumHoursPerDay = new TreeMap<>();
        log.info("Начало расчета количества часов полета за 1 день");
        for (Flight flight : flights) {
            LocalDateTime departureTime = flight.getDepartureTime();
            LocalDateTime arrivalTime = flight.getArrivalTime();

            try {
                flightValidator.validate(flight);
            } catch (IllegalArgumentException e) {
                log.warn("Рейс {} пропущен: {}", flight.getId(), e.getMessage());
                continue; // Пропускаем этот рейс
            }

            log.debug("Обрабатываем рейс с ID: {} от {} до {}", flight.getId(), departureTime, arrivalTime);

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
                    log.debug("Время полета для дня {}: {} часов", departureDay, durationInHoursDay);
                    departureTime = nextDay;
                } else {
                    long durationInHours = Duration.between(departureTime, nextDay).toHours();
                    sumHoursPerDay.merge(departureDay, durationInHours, Long::sum);
                    log.debug("Часы полета для дня {}: {} часов", departureDay, durationInHours);
                    // Обновляем начало на первый день следующего месяца
                    departureTime = nextDay;
                }
            }
        }
        log.info("Завершен расчет количества часов полета за 1 день");
        return sumHoursPerDay;
    }

    //TODO original
    // Возвращает количество часов полёта за 7 дней
    private Map<LocalDate, Long> getHoursPerWeek(Map<LocalDate, Long> hoursPerDay) {
        Map<LocalDate, Long> sumHoursPerWeek = new TreeMap<>();
        log.info("Начало расчета количества часов полета за неделю");
        for (Map.Entry<LocalDate, Long> hoursPerWeek : hoursPerDay.entrySet()) {
            // Конкретный день
            LocalDate weekday = hoursPerWeek.getKey();

            // Количество часов полета в день
            Long hours = hoursPerWeek.getValue();

            // Рассчитываем начало недели (понедельник)
            LocalDate startOfWeek = weekday.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

            sumHoursPerWeek.merge(startOfWeek, hours, Long::sum);
            log.debug("Дата: {} | Часы: {} | Начало недели: {}", weekday, hours, startOfWeek);
        }
        log.info("Завершен расчет количества часов полета за неделю");
        return sumHoursPerWeek;
    }

    //TODO original
    // Возвращает количество часов полёта за месяц
    private Map<LocalDate, Long> getHoursPerMonths(Map<LocalDate, Long> hoursPerDay) {
        Map<LocalDate, Long> sumHoursPerMonth = new TreeMap<>();
        log.info("Начало расчета количества часов полета за месяц");
        for (Map.Entry<LocalDate, Long> hoursPerMonth : hoursPerDay.entrySet()) {
            LocalDate month = hoursPerMonth.getKey();
            Long hours = hoursPerMonth.getValue();
            // Рассчитываем начало месяца
            LocalDate startOfMonth = month.with(TemporalAdjusters.firstDayOfMonth());
            // Добавляем количество часов в карту по началу месяца
            sumHoursPerMonth.merge(startOfMonth, hours, Long::sum);
            log.debug("Дата: {} | Часы: {} | Начало месяца: {}", month, hours, startOfMonth);
        }
        log.info("Завершен расчет количества часов полета за месяц");
        return sumHoursPerMonth;
    }
}