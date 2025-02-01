package ru.example.validator;

import ru.example.model.Pilot;

/**
 * Класс для валидации объектов типа {@link Pilot}.
 * Этот класс проверяет корректность различных параметров пилота, таких как его идентификатор,
 * полное имя и соответствие строковых значений требованиям.
 *
 * <p>Валидация включает в себя проверку на null, проверку на положительность идентификатора,
 * корректность формата имени (буквы и пробелы), а также проверку длины имени.</p>
 *
 * @see Pilot
 */
public class PilotValidator implements Validator<Pilot> {

    /**
     * Выполняет валидацию переданного объекта пилота.
     * Проверяет, что все обязательные поля корректны и не содержат ошибок.
     *
     * @param pilot Объект пилота, который должен быть валиден.
     * @throws IllegalArgumentException если один из параметров пилота некорректен.
     * <ul>
     *   <li>Идентификатор пилота не может быть null;</li>
     *   <li>Идентификатор пилота должен быть положительным;</li>
     *   <li>Полное имя не может быть null или пустым;</li>
     *   <li>Полное имя должно содержать только буквы и пробелы;</li>
     *   <li>Полное имя должно содержать хотя бы два слова;</li>
     *   <li>Полное имя не может быть длиннее 100 символов;</li>
     * </ul>
     */
    @Override
    public void validate(Pilot pilot) {
        // Проверка идентификатора пилота на null
        if (pilot.getIdPilot() == null) {
            throw new IllegalArgumentException("Идентификатор пилота не может быть null.");
        }

        // Проверка на положительность идентификатора пилота
        if (pilot.getIdPilot() <= 0) {
            throw new IllegalArgumentException("Идентификатор пилота должен быть положительным числом.");
        }

        // Проверка полного имени на null
        if (pilot.getFullName() == null) {
            throw new IllegalArgumentException("Полное имя не может быть null.");
        }

        // Проверка пустоты полного имени
        if (pilot.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Полное имя не может быть пустым.");
        }

        // Проверка на правильность формата имени (только буквы и пробелы)
        if (!pilot.getFullName().matches("[a-zA-Zа-яА-ЯёЁ\\s]+")) {
            throw new IllegalArgumentException("Полное имя может содержать только буквы и пробелы.");
        }

        // Проверка, что имя содержит хотя бы два слова
        if (pilot.getFullName().split("\\s+").length < 2) {
            throw new IllegalArgumentException("Полное имя должно содержать хотя бы два слова.");
        }

        // Проверка длины полного имени
        if (pilot.getFullName().length() > 100) {
            throw new IllegalArgumentException("Полное имя не может быть длиннее 100 символов.");
        }
    }
}
