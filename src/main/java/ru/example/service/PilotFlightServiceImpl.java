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

/**
 * Реализация сервиса для обработки данных пилотов и рейсов. Сервис предоставляет функциональность для
 * валидации и сохранения данных пилотов и рейсов, а также для вычисления времени налета по месяцам,
 * неделям и дням.
 *
 * <p>Методы сервиса выполняют следующие действия:
 * <ul>
 *     <li>Валидация пилотов и рейсов</li>
 *     <li>Связывание пилотов с рейсами</li>
 *     <li>Расчет общего времени полетов по дням, неделям и месяцам</li>
 *     <li>Сохранение данных в объект {@link OutputPilotsAndFlights}</li>
 * </ul>
 *
 * <p>Методы сервиса ведут логирование на разных уровнях (INFO, DEBUG, WARN, ERROR) для отслеживания
 * процессов валидации и обработки данных.
 *
 * @see FlightValidator
 * @see PilotValidator
 * @see Flight
 * @see Pilot
 * @see OutputPilotsAndFlights
 */
@Slf4j
public class PilotFlightServiceImpl implements PilotFlightService {

    private final Map<Pilot, List<Flight>> flightsByPilot = new HashMap<>();
    private final FlightValidator flightValidator = new FlightValidator();
    private final PilotValidator pilotValidator = new PilotValidator();

    /**
     * Обрабатывает данные о пилотах и рейсах, включая валидацию и сохранение информации в {@link OutputPilotsAndFlights}.
     *
     * @param inputPilotsAndFlights объект, содержащий данные о пилотах и рейсах
     * @return объект {@link OutputPilotsAndFlights}, содержащий обработанные данные пилотов и рейсов
     */
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

    /**
     * Сохраняет данные пилотов и их налет в объект {@link OutputPilotsAndFlights}.
     * Для каждого пилота рассчитывается налет по месяцам.
     *
     * @param outputPilotsAndFlights объект для сохранения данных о пилотах и их налете
     */
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

    /**
     * Сохраняет данные пилотов и рейсов, валидация которых была успешной.
     * Также связывает пилотов с рейсами.
     *
     * @param inputPilotsAndFlights объект, содержащий данные о пилотах и рейсах
     */
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

    /**
     * Рассчитывает общее количество часов налета по месяцам на основе списка рейсов.
     * Для каждого месяца вычисляются: общее количество часов, количество полетов, а также флаги
     * для проверки превышения лимитов по дням, неделям и месяцам.
     *
     * @param flights список рейсов
     * @return список объектов {@link TimeMonth}, содержащих информацию о налете по месяцам
     */
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
        log.info("Завершен расчет налета по месяцам");
        return timeMonths;
    }

    /**
     * Возвращает количество часов полёта за 1 день.
     *
     * Метод рассчитывает количество часов полета для каждого дня на основе списка рейсов.
     * Для каждого рейса проверяется время вылета и прилёта, затем это время добавляется к общей сумме для каждого дня.
     *
     * @param flights Список рейсов, для которых необходимо рассчитать количество часов полета.
     * @return Карта, где ключ — это день (LocalDate), а значение — общее количество часов полета в этот день.
     */
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

    /**
     * Возвращает количество часов полёта за 7 дней.
     *
     * Метод рассчитывает количество часов полета за неделю, группируя часы полета по началу недели (понедельник).
     *
     * @param hoursPerDay Карта, где ключ — это день, а значение — количество часов полета в этот день.
     * @return Карта, где ключ — это начало недели (понедельник), а значение — общее количество часов полета в эту неделю.
     */
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

    /**
     * Возвращает количество часов полёта за месяц.
     *
     * Метод рассчитывает количество часов полета за месяц, группируя часы полета по началу месяца.
     *
     * @param hoursPerDay Карта, где ключ — это день, а значение — количество часов полета в этот день.
     * @return Карта, где ключ — это начало месяца, а значение — общее количество часов полета в этот месяц.
     */
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