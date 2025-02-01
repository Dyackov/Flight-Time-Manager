package ru.example.validator;

import ru.example.model.Flight;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Класс для валидации объектов типа {@link Flight}.
 * Этот класс проверяет корректность различных параметров рейса, таких как время отправления,
 * время прибытия, идентификаторы пилотов, аэропорты и другие важные атрибуты рейса.
 *
 * <p>Валидация включает в себя проверку на null, проверку правильности времени рейса,
 * корректности идентификаторов пилотов, а также проверки на пустоту строковых полей.</p>
 *
 * @see Flight
 */
public class FlightValidator implements Validator<Flight> {

    /**
     * Выполняет валидацию переданного объекта рейса.
     * Проверяет, что все обязательные поля корректны и не содержат ошибок.
     *
     * @param flight Объект рейса, который должен быть валиден.
     * @throws IllegalArgumentException если один из параметров рейса некорректен.
     * <ul>
     *   <li>Рейс не может быть null;</li>
     *   <li>Идентификатор рейса не должен быть null;</li>
     *   <li>Тип и номер самолета не могут быть пустыми;</li>
     *   <li>Время отправления и прибытия не должны быть null;</li>
     *   <li>Время прибытия не должно быть раньше времени отправления;</li>
     *   <li>Дата отправления не может быть позже даты прибытия;</li>
     *   <li>Аэропорты отправления и прибытия не могут быть пустыми;</li>
     *   <li>Список пилотов не должен быть пустым, идентификаторы пилотов должны быть положительными;</li>
     * </ul>
     */
    @Override
    public void validate(Flight flight) {

        // Проверка на null
        if (flight == null) {
            throw new IllegalArgumentException("Рейс не может быть null.");
        }

        // Проверка идентификатора рейса
        if (flight.getId() == null) {
            throw new IllegalArgumentException("Идентификатор рейса не должен быть null.");
        }

        // Проверка типа самолета
        if (flight.getAircraftType() == null || flight.getAircraftType().isEmpty()) {
            throw new IllegalArgumentException("Тип самолёта не должен быть пустым.");
        }

        // Проверка номера самолета
        if (flight.getAircraftNumber() == null || flight.getAircraftNumber().isEmpty()) {
            throw new IllegalArgumentException("Номер самолёта не должен быть пустым.");
        }

        // Проверка времени отправления и прибытия
        if (flight.getDepartureTime() == null || flight.getArrivalTime() == null) {
            throw new IllegalArgumentException("Время отправления / время прибытия не должны быть null.");
        }

        LocalDateTime start = flight.getDepartureTime();
        LocalDateTime end = flight.getArrivalTime();

        // Проверка, что время отправления не позже времени прибытия
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("Время отправления не должно быть позже времени прибытия.");
        }

        // Проверка на соответствие месяцев отправления и прибытия
        if (start.toLocalDate().isAfter(end.toLocalDate())) {
            throw new IllegalArgumentException("Месяц отправления не должен быть после месяца прибытия.");
        }

        // Проверка, что время начала рейса раньше времени окончания
        if (start.isAfter(end) || start.isEqual(end)) {
            throw new IllegalArgumentException("Время начала должно быть раньше времени окончания.");
        }

        // Проверка аэропорта отправления
        if (flight.getDepartureAirport() == null || flight.getDepartureAirport().isEmpty()) {
            throw new IllegalArgumentException("Аэропорт отправления не должен быть пустым.");
        }

        // Проверка аэропорта прибытия
        if (flight.getArrivalAirport() == null || flight.getArrivalAirport().isEmpty()) {
            throw new IllegalArgumentException("Аэропорт прибытия не должен быть пустым.");
        }

        // Проверка списка пилотов
        List<Long> idPilots = flight.getIdPilots();
        if (idPilots == null || idPilots.isEmpty()) {
            throw new IllegalArgumentException("Список пилотов не должен быть пустым.");
        }

        // Проверка корректности идентификаторов пилотов
        for (Long pilotId : idPilots) {
            if (pilotId == null || pilotId <= 0) {
                throw new IllegalArgumentException("Идентификатор пилота должен быть положительным и отличным от null.");
            }
        }
    }
}
