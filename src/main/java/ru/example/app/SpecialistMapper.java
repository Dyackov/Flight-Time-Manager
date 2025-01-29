//package ru.example.app;
//
//import ru.example.model.Flight;
//import ru.example.model.Pilot;
//import ru.example.model.Specialist;
//import ru.example.model.TimeMonth;
//
//import java.time.Duration;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.temporal.WeekFields;
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class SpecialistMapper {
//
//    public List<Specialist> mapToSpecialists(Map<Pilot, List<Flight>> flightsByPilot) {
//        List<Specialist> specialists = new ArrayList<>();
//
//        for (Map.Entry<Pilot, List<Flight>> entry : flightsByPilot.entrySet()) {
//            Pilot pilot = entry.getKey();
//            List<Flight> flights = entry.getValue();
//
//            // Группируем перелёты по месяцам
//            Map<LocalDate, List<Flight>> flightsByMonth = flights.stream()
//                .collect(Collectors.groupingBy(flight -> flight.getDepartureTime().toLocalDate().withDayOfMonth(1)));
//
//            // Создаём объект Specialist для пилота
//            Specialist specialist = new Specialist();
//            specialist.setId(pilot.getId());
//            specialist.setFirstName(pilot.getFirstName());
//            specialist.setLastName(pilot.getLastName());
//            specialist.setTimeMonths(new ArrayList<>()); // Инициализируем список TimeMonth
//
//            // Обрабатываем данные для каждого месяца
//            for (Map.Entry<LocalDate, List<Flight>> monthEntry : flightsByMonth.entrySet()) {
//                LocalDate month = monthEntry.getKey();
//                List<Flight> monthlyFlights = monthEntry.getValue();
//
//                // Подсчитываем общее время за месяц
//                int totalFlightHours = monthlyFlights.stream()
//                    .mapToInt(this::calculateFlightDurationInHours)
//                    .sum();
//
//                // Проверяем превышения
//                boolean exceedsMonthlyLimit = totalFlightHours > 80;
//                boolean exceedsWeeklyLimit = hasWeeklyExceed(monthlyFlights);
//                boolean exceedsDailyLimit = hasDailyExceed(monthlyFlights);
//
//                // Создаём объект TimeMonth для месяца
//                TimeMonth timeMonth = new TimeMonth();
//                timeMonth.setMonth(month);
//                timeMonth.setTotalFlightHours(totalFlightHours);
//
//                // Добавляем TimeMonth в список
//                specialist.getTimeMonths().add(timeMonth);
//
//                // Устанавливаем флаги превышения
//                specialist.setExceedsMonthlyLimit(exceedsMonthlyLimit);
//                specialist.setExceedsWeeklyLimit(exceedsWeeklyLimit);
//                specialist.setExceedsDailyLimit(exceedsDailyLimit);
//            }
//
//            // Добавляем специалиста в список
//            specialists.add(specialist);
//        }
//
//        return specialists;
//    }
//
//    private int calculateFlightDurationInHours(Flight flight) {
//        // Вычисляем продолжительность полёта в часах
//        Duration duration = Duration.between(flight.getDepartureTime(), flight.getArrivalTime());
//        return (int) duration.toHours();
//    }
//
//    private boolean hasWeeklyExceed(List<Flight> flights) {
//        // Группируем перелёты по неделям
//        Map<Integer, Integer> weeklyHours = flights.stream()
//            .collect(Collectors.groupingBy(
//                flight -> flight.getDepartureTime().get(WeekFields.ISO.weekOfWeekBasedYear()),
//                Collectors.summingInt(this::calculateFlightDurationInHours)
//            ));
//
//        // Проверяем, есть ли недели с превышением
//        return weeklyHours.values().stream().anyMatch(hours -> hours > 36);
//    }
//
//    private boolean hasDailyExceed(List<Flight> flights) {
//        // Группируем перелёты по дням
//        Map<LocalDate, Integer> dailyHours = flights.stream()
//            .collect(Collectors.groupingBy(
//                flight -> flight.getDepartureTime().toLocalDate(),
//                Collectors.summingInt(this::calculateFlightDurationInHours)
//            ));
//
//        // Проверяем, есть ли дни с превышением
//        return dailyHours.values().stream().anyMatch(hours -> hours > 8);
//    }
//}
//
//
