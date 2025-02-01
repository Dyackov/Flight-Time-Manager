package ru.example.validator;

import ru.example.model.Flight;

import java.time.LocalDateTime;
import java.util.List;

public class FlightValidator implements Validator<Flight> {

    @Override
    public void validate(Flight flight) {

        if (flight == null) {
            throw new IllegalArgumentException("Рейс не может быть null.");
        }

        if (flight.getId() == null) {
            throw new IllegalArgumentException("Идентификатор рейса не должен быть null.");
        }

        if (flight.getAircraftType() == null || flight.getAircraftType().isEmpty()) {
            throw new IllegalArgumentException("Тип самолёта не должен быть пустым.");
        }

        if (flight.getAircraftNumber() == null || flight.getAircraftNumber().isEmpty()) {
            throw new IllegalArgumentException("Номер самолёта не должен быть пустым.");
        }

        if (flight.getDepartureTime() == null || flight.getArrivalTime() == null) {
            throw new IllegalArgumentException("Время отправления / время прибытия не должны быть null.");
        }

        LocalDateTime start = flight.getDepartureTime();
        LocalDateTime end = flight.getArrivalTime();


        if (end.isBefore(start)) {
            throw new IllegalArgumentException("Время отправления не должно быть позже времени прибытия.");
        }

        if (start.toLocalDate().isAfter(end.toLocalDate())) {
            throw new IllegalArgumentException("Месяц отправления не должен быть после месяца прибытия.");
        }

        if (start.isAfter(end) || start.isEqual(end)) {
            throw new IllegalArgumentException("Время начала должно быть раньше времени окончания.");
        }

        if (flight.getDepartureAirport() == null || flight.getDepartureAirport().isEmpty()) {
            throw new IllegalArgumentException("Аэропорт отправления не должен быть пустым.");
        }

        if (flight.getArrivalAirport() == null || flight.getArrivalAirport().isEmpty()) {
            throw new IllegalArgumentException("Аэропорт прибытия не должен быть пустым.");
        }

        List<Long> idPilots = flight.getIdPilots();
        if (idPilots == null || idPilots.isEmpty()) {
            throw new IllegalArgumentException("Список пилотов не должен быть пустым.");
        }

        for (Long pilotId : idPilots) {
            if (pilotId == null || pilotId <= 0) {
                throw new IllegalArgumentException("Идентификатор пилота должен быть положительным и отличным от null.");
            }
        }
    }
}