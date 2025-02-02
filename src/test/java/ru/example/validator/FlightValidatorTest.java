package ru.example.validator;

import org.junit.jupiter.api.Test;
import ru.example.model.Flight;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FlightValidatorTest {

    private final FlightValidator validator = new FlightValidator();

    @Test
    void validate_ValidFlight_NoExceptionThrown() {
        Flight validFlight = new Flight(1L, "Boeing 737", "ABC123",
                LocalDateTime.of(2023, 10, 1, 10, 0),
                LocalDateTime.of(2023, 10, 1, 12, 0),
                "SVO", "LED", List.of(1L, 2L));
        assertDoesNotThrow(() -> validator.validate(validFlight));
    }

    @Test
    void validate_NullFlight_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validator.validate(null));
        assertEquals("Рейс не может быть null.", exception.getMessage());
    }

    @Test
    void validate_NullId_ThrowsException() {
        Flight flight = new Flight(null, "Boeing 737", "ABC123",
                LocalDateTime.of(2023, 10, 1, 10, 0),
                LocalDateTime.of(2023, 10, 1, 12, 0),
                "SVO", "LED", List.of(1L, 2L));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validator.validate(flight));
        assertEquals("Идентификатор рейса не должен быть null.", exception.getMessage());
    }

    @Test
    void validate_NullAircraftType_ThrowsException() {
        Flight flight = new Flight(1L, null, "ABC123",
                LocalDateTime.of(2023, 10, 1, 10, 0),
                LocalDateTime.of(2023, 10, 1, 12, 0),
                "SVO", "LED", List.of(1L, 2L));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validator.validate(flight));
        assertEquals("Тип самолёта не должен быть пустым.", exception.getMessage());
    }

    @Test
    void validate_EmptyAircraftNumber_ThrowsException() {
        Flight flight = new Flight(1L, "Boeing 737", "",
                LocalDateTime.of(2023, 10, 1, 10, 0),
                LocalDateTime.of(2023, 10, 1, 12, 0),
                "SVO", "LED", List.of(1L, 2L));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validator.validate(flight));
        assertEquals("Номер самолёта не должен быть пустым.", exception.getMessage());
    }

    @Test
    void validate_NullDepartureTime_ThrowsException() {
        Flight flight = new Flight(1L, "Boeing 737", "ABC123",
                null,
                LocalDateTime.of(2023, 10, 1, 12, 0),
                "SVO", "LED", List.of(1L, 2L));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validator.validate(flight));
        assertEquals("Время отправления / время прибытия не должны быть null.", exception.getMessage());
    }

    @Test
    void validate_ArrivalBeforeDeparture_ThrowsException() {
        Flight flight = new Flight(1L, "Boeing 737", "ABC123",
                LocalDateTime.of(2023, 10, 1, 12, 0),
                LocalDateTime.of(2023, 10, 1, 10, 0),
                "SVO", "LED", List.of(1L, 2L));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validator.validate(flight));
        assertEquals("Время отправления не должно быть позже времени прибытия.", exception.getMessage());
    }

    @Test
    void validate_EmptyDepartureAirport_ThrowsException() {
        Flight flight = new Flight(1L, "Boeing 737", "ABC123",
                LocalDateTime.of(2023, 10, 1, 10, 0),
                LocalDateTime.of(2023, 10, 1, 12, 0),
                "", "LED", List.of(1L, 2L));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validator.validate(flight));
        assertEquals("Аэропорт отправления не должен быть пустым.", exception.getMessage());
    }

    @Test
    void validate_EmptyArrivalAirport_ThrowsException() {
        Flight flight = new Flight(1L, "Boeing 737", "ABC123",
                LocalDateTime.of(2023, 10, 1, 10, 0),
                LocalDateTime.of(2023, 10, 1, 12, 0),
                "SVO", "", List.of(1L, 2L));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validator.validate(flight));
        assertEquals("Аэропорт прибытия не должен быть пустым.", exception.getMessage());
    }

    @Test
    void validate_EmptyPilotList_ThrowsException() {
        Flight flight = new Flight(1L, "Boeing 737", "ABC123",
                LocalDateTime.of(2023, 10, 1, 10, 0),
                LocalDateTime.of(2023, 10, 1, 12, 0),
                "SVO", "LED", List.of());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validator.validate(flight));
        assertEquals("Список пилотов не должен быть пустым.", exception.getMessage());
    }

    @Test
    void validate_NegativePilotId_ThrowsException() {
        Flight flight = new Flight(1L, "Boeing 737", "ABC123",
                LocalDateTime.of(2023, 10, 1, 10, 0),
                LocalDateTime.of(2023, 10, 1, 12, 0),
                "SVO", "LED", List.of(-1L));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validator.validate(flight));
        assertEquals("Идентификатор пилота должен быть положительным и отличным от null.", exception.getMessage());
    }
}