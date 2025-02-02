package ru.example.validator;

import org.junit.jupiter.api.Test;
import ru.example.model.Pilot;

import static org.junit.jupiter.api.Assertions.*;

class PilotValidatorTest {

    private final PilotValidator validator = new PilotValidator();

    @Test
    void validate_ValidPilot_NoExceptionThrown() {
        Pilot validPilot = new Pilot(1L, "Иван Иванов", null);
        assertDoesNotThrow(() -> validator.validate(validPilot));
    }

    @Test
    void validate_NullId_ThrowsException() {
        Pilot pilot = new Pilot(null, "Иван Иванов", null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validator.validate(pilot));
        assertEquals("Идентификатор пилота не может быть null.", exception.getMessage());
    }

    @Test
    void validate_NegativeId_ThrowsException() {
        Pilot pilot = new Pilot(-1L, "Иван Иванов", null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validator.validate(pilot));
        assertEquals("Идентификатор пилота должен быть положительным числом.", exception.getMessage());
    }

    @Test
    void validate_NullFullName_ThrowsException() {
        Pilot pilot = new Pilot(1L, null, null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validator.validate(pilot));
        assertEquals("Полное имя не может быть null.", exception.getMessage());
    }

    @Test
    void validate_EmptyFullName_ThrowsException() {
        Pilot pilot = new Pilot(1L, "   ", null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validator.validate(pilot));
        assertEquals("Полное имя не может быть пустым.", exception.getMessage());
    }

    @Test
    void validate_FullNameWithNumbers_ThrowsException() {
        Pilot pilot = new Pilot(1L, "Иван Иванов123", null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validator.validate(pilot));
        assertEquals("Полное имя может содержать только буквы и пробелы.", exception.getMessage());
    }

    @Test
    void validate_SingleWordFullName_ThrowsException() {
        Pilot pilot = new Pilot(1L, "Иван", null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validator.validate(pilot));
        assertEquals("Полное имя должно содержать хотя бы два слова.", exception.getMessage());
    }

    @Test
    void validate_FullNameTooLong_ThrowsException() {
        String longName = "И И".repeat(101);
        Pilot pilot = new Pilot(1L, longName, null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validator.validate(pilot));
        assertEquals("Полное имя не может быть длиннее 100 символов.", exception.getMessage());
    }
}