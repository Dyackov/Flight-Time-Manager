package ru.example.service;

import ru.example.model.*;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

public class PilotFlightServiceImpl implements PilotFlightService {

    private final Map<Pilot, List<Flight>> flightsByPilot = new HashMap<>();

    public OutputPilotsAndFlights process(InputPilotsAndFlights inputPilotsAndFlights) {
        OutputPilotsAndFlights outputPilotsAndFlights = new OutputPilotsAndFlights();

        // Распределение в маппу
        savedPilotAndFlights(inputPilotsAndFlights);

        // сохранение в класс Specialist
        savedOutPut(outputPilotsAndFlights);


        return outputPilotsAndFlights;
    }

    public void savedPilotAndFlights(InputPilotsAndFlights inputPilotsAndFlights) {
        Map<Long, Pilot> pilotById = inputPilotsAndFlights.getPilots().stream()
                .collect(Collectors.toMap(Pilot::getIdPilot, pilot -> pilot));

        inputPilotsAndFlights.getFlights().forEach(flight -> {
            flight.getIdPilots().forEach(pilotId -> {
                Pilot pilot = pilotById.get(pilotId);
                if (pilot != null) {
                    flightsByPilot.computeIfAbsent(pilot, k -> new ArrayList<>()).add(flight);
                }
            });
        });
    }

    public void savedOutPut(OutputPilotsAndFlights outputPilotsAndFlights) {
/*        List<Specialist> specialists = new ArrayList<>();

        for (Map.Entry<Pilot, List<Flight>> entry : flightsByPilot.entrySet()) {
            // конкретный пилот
            Pilot pilot = entry.getKey();
            // полёты конкретного пилота
            List<Flight> flights = entry.getValue();

            // собираем выходной класс
            Specialist specialist = Specialist.builder()
                    .id(pilot.getIdPilot())
                    .firstName(pilot.getFirstName())
                    .lastName(pilot.getLastName())
                    .timeMonths(new ArrayList<>())
                    .build();

            Map<LocalDate, Integer> sumHoursMonth = new HashMap<>();

            for (Flight flight : flights) {
                Map<LocalDate, Integer> splitFlightHours = splitFlightByMonth(flight);
                for (Map.Entry<LocalDate, Integer> monthEntry : splitFlightHours.entrySet()) {
                    LocalDate month = monthEntry.getKey();
                    Integer flightHours = monthEntry.getValue();

                    sumHoursMonth.merge(month, flightHours, Integer::sum);
                }
            }

            for (Map.Entry<LocalDate, Integer> monthEntry : sumHoursMonth.entrySet()) {
                LocalDate month = monthEntry.getKey();
                Integer flightHours = monthEntry.getValue();

                boolean exceedsMonthlyLimit = flightHours > 80;
                boolean exceedsWeeklyLimit = flightHours > 36;
                boolean exceedsDailyLimit = flightHours > 8;

                TimeMonth timeMonth = TimeMonth.builder()
                        .month(month)
                        .totalFlightHours(flightHours)
                        .exceedsMonthlyLimit(exceedsMonthlyLimit)
                        .exceedsWeeklyLimit(exceedsWeeklyLimit)
                        .exceedsDailyLimit(exceedsDailyLimit)
                        .build();
                specialist.getTimeMonths().add(timeMonth);
                specialists.add(specialist);
            }

        }*/
        for (Map.Entry<Pilot, List<Flight>> entry : flightsByPilot.entrySet()) {

            // конкретный пилот
            Pilot pilot = entry.getKey();
            // полёты конкретного пилота
            List<Flight> flights = entry.getValue();

            // Рассчитываем данные по налету
            List<TimeMonth> timeMonths = calculateFlightTime(flights);

            pilot.setTimeMonthList(timeMonths);


//            Map<LocalDate, Long> sumHoursPerDay = getHoursPerDay(flights);
//            Map<LocalDate, Long> sumHoursPerWeek = getHoursPerWeek(sumHoursPerDay);
//            Map<LocalDate, Long> sumHoursPerMonth = getHoursPerMonths(sumHoursPerWeek);
//
//
//
//            for (Map.Entry<LocalDate,Long> rrr : sumHoursPerMonth.entrySet()) {
//                LocalDate date = rrr.getKey();
//                Long hours = rrr.getValue();
//                TimeMonth timeMonth = new TimeMonth();
//                timeMonth.setDate(date);
//                timeMonth.setTotalFlightHours(hours);
//                timeMonth.setExceedsMonthlyLimit(hours >= 80);
//
//                pilot.getTimeMonthList().add(timeMonth);
//            }


            // Получение месяца и кол-во часов полёта
//            Map<LocalDate, Long> hoursPerMonth = getHoursPerMonth(flights);

            // тест получение дня и кол-ва часов полёта//           Map<LocalDate, Long> hoursPerDay = getHoursPerDay(flights);

            // Сеттинг полётов пилоту
//            List<TimeMonth> timeMonths = addFlightHoursToPilot(pilot, hoursPerMonth);


            outputPilotsAndFlights.getSpecialists().add(pilot);
        }
    }


//    // возвращает месяц и кол во часов полёта
//    private List<TimeMonth> addFlightHoursToPilot(Pilot pilot, Map<LocalDate, Long> hoursPerMonth) {
//        List<TimeMonth> timeMonths = new ArrayList<>();
//
//        for (Map.Entry<LocalDate, Long> monthsHours : hoursPerMonth.entrySet()) {
//            LocalDate month = monthsHours.getKey();
//            Long hours = monthsHours.getValue();
//
//            TimeMonth timeMonth = new TimeMonth(month, hours);
//
//            timeMonths.add(timeMonth);
//            pilot.getTimeMonthList().add(timeMonth);
//        }
//        return timeMonths;
//    }

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

        for (Map.Entry<LocalDate, Long> monthEntry : hoursPerMonth.entrySet()) {
            LocalDate month = monthEntry .getKey();
            Long totalMonthHours  = monthEntry .getValue();


            // Флаг превышения месячного лимита
            boolean exceedsMonthlyLimit = totalMonthHours > 80;

            // Создаем объект TimeMonth
            TimeMonth timeMonth = new TimeMonth();
            timeMonth.setDate(month);
            timeMonth.setTotalFlightHours(totalMonthHours);
            timeMonth.setExceedsMonthlyLimit(exceedsMonthlyLimit);

            // 5. Проверяем недельный лимит (в рамках этого месяца)
            long maxWeeklyHours = hoursPerWeek.entrySet().stream()
                    .filter(entry -> {
                        LocalDate currentWeekDate = entry.getKey();
                        // Преобразуем обе даты в YearMonth
                        YearMonth currentMonth = YearMonth.from(month);
                        YearMonth entryMonth = YearMonth.from(currentWeekDate);

                        // Если неделя попадает в тот же месяц, либо переход через месяц
                        return entryMonth.equals(currentMonth);
                    })
                    .mapToLong(Map.Entry::getValue)
                    .max()
                    .orElse(0);

            timeMonth.setExceedsWeeklyLimit(maxWeeklyHours > 36);

            // 6. Проверяем дневной лимит (в рамках этого месяца)
            long maxDayHoursForMonth = hoursPerDay.entrySet().stream()
                    .filter(entry -> {
                        // Проверяем, попадает ли день в текущий месяц
                        LocalDate day = entry.getKey();
                        return day.getMonth().equals(month.getMonth());
                    })
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

            while (departureTime.isBefore(arrivalTime)) {
                // День вылета
                LocalDate departureDay = departureTime.toLocalDate();
                // День прилёта
                LocalDate arrivalDay = arrivalTime.toLocalDate();
                // След. день от дня вылета
                LocalDateTime nextDay = departureDay.plusDays(1).atStartOfDay().withHour(0).withMinute(0).withSecond(0);

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


    //TODO подумать убрать ли
    // возвращает месяц и кол-во часов полёта
    private Map<LocalDate, Long> getHoursPerMonth(List<Flight> flights) {
        Map<LocalDate, Long> sumHoursPerMonth = new HashMap<>();

        for (Flight flight : flights) {
            LocalDateTime start = flight.getDepartureTime();
            LocalDateTime end = flight.getArrivalTime();
            validateDateTime(start, end);
//            getHoursPerDay(flight,start.toLocalDate(),end.toLocalDate());


            while (start.isBefore(end)) {
                // Получаем месяц и год начала текущего отрезка
                LocalDate monthYearStart = start.toLocalDate().withDayOfMonth(1);

                // Рассчитываем конец текущего месяца
                LocalDateTime endOfMonth = start.plusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

                if (end.isBefore(endOfMonth)) {
                    // Полёт заканчивается в текущем месяце
                    long durationInHours = Duration.between(start, end).toHours();
                    sumHoursPerMonth.merge(monthYearStart, durationInHours, Long::sum);
                    break; // Завершаем обработку текущего полёта
                } else {
                    // Полёт продолжается в следующем месяце
                    long durationInHours = Duration.between(start, endOfMonth).toHours();
                    sumHoursPerMonth.merge(monthYearStart, durationInHours, Long::sum);

                    // Обновляем начало на первый день следующего месяца
                    start = endOfMonth;
                }
            }
        }
        return sumHoursPerMonth;
    }

/*    private Map<LocalDate, Integer> splitFlightByMonth(Flight flight) {
        Map<LocalDate, Integer> flightsByMonth = new HashMap<>();

        // дата и время вылета
        LocalDateTime departureTime = flight.getDepartureTime();
        // дата и время прилёта
        LocalDateTime arrivalTime = flight.getArrivalTime();

        validateDateTime(departureTime, arrivalTime);

        // месяц вылета
        LocalDate startDate = departureTime.toLocalDate().withDayOfMonth(1);
        // месяц прилёта
        LocalDate endDate = arrivalTime.toLocalDate().withDayOfMonth(1);

        // Обработка полёта, который пересекает несколько месяцев
        while (!startDate.isAfter(endDate)) {
            // Начало текущего месяца
            LocalDateTime startOfMonth = startDate.atStartOfDay();

            // Конец текущего месяца (включая последний момент)
            LocalDateTime endOfMonth = startDate.withDayOfMonth(startDate.lengthOfMonth()).atTime(23, 59, 59);

            System.out.println(" Конец текущего месяца : " + endOfMonth);
            System.out.println(" Конец текущего месяца2222222 : " + startDate.lengthOfMonth());

            // Время начала полёта в текущем месяце
            LocalDateTime flightStart = departureTime.isAfter(startOfMonth) ? departureTime : startOfMonth;

            // Время окончания полёта в текущем месяце
            LocalDateTime flightEnd = arrivalTime.isBefore(endOfMonth) ? arrivalTime : endOfMonth;
            System.out.println("124: " + flightEnd);

            // Если полёт не выходит за рамки месяца
            if (!flightStart.isAfter(flightEnd)) {
                Duration duration = Duration.between(flightStart, flightEnd);
                int hours = (int) duration.toHours();

                // Логирование для отслеживания
                System.out.println("Flight start: " + flightStart);
                System.out.println("Flight end: " + flightEnd);
                System.out.println("Flight duration (hours): " + hours);

                flightsByMonth.merge(startDate, hours, Integer::sum); // Учитываем возможные пересечения
            }

            // Переход к следующему месяцу
            startDate = startDate.plusMonths(1);
        }

        return flightsByMonth;
    }*/

    private void validateDateTime(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Время отправления и время прибытия не должны быть нулевыми.");
        }
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("Время отправления не должно быть позже времени прибытия.");
        }
        if (start.toLocalDate().isAfter(end.toLocalDate())) {
            throw new IllegalArgumentException("Месяц отправления не должен быть после месяца прибытия.");
        }
        if (start.isAfter(end) || start.isEqual(end)) {
            throw new IllegalArgumentException("Время начала должно быть раньше времени окончания.");
        }

    }
}

