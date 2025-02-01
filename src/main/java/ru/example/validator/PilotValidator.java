package ru.example.validator;

import ru.example.model.Pilot;

public class PilotValidator implements Validator<Pilot> {

    @Override
    public void validate(Pilot pilot) {
        if (pilot.getIdPilot() == null) {
            throw new IllegalArgumentException("Идентификатор пилота не может быть null.");
        }

        if (pilot.getIdPilot() <= 0) {
            throw new IllegalArgumentException("Идентификатор пилота должен быть положительным числом.");
        }

        if (pilot.getFullName() == null) {
            throw new IllegalArgumentException("Полное имя не может быть null.");
        }

        if (pilot.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Полное имя не может быть пустым.");
        }

        if (!pilot.getFullName().matches("[a-zA-Zа-яА-ЯёЁ\\s]+")) {
            throw new IllegalArgumentException("Полное имя может содержать только буквы и пробелы.");
        }

        if (pilot.getFullName().split("\\s+").length < 2) {
            throw new IllegalArgumentException("Полное имя должно содержать хотя бы два слова.");
        }

        if (pilot.getFullName().length() > 100) {
            throw new IllegalArgumentException("Полное имя не может быть длиннее 100 символов.");
        }
    }
}