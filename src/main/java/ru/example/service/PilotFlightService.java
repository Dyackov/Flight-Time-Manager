package ru.example.service;

import ru.example.dto.InputPilotsAndFlights;
import ru.example.dto.OutputPilotsAndFlights;

/**
 * Сервис для обработки данных о пилотах и рейсах.
 * <p>
 * Этот интерфейс предоставляет метод для обработки информации о пилотах и рейсах, преобразуя
 * входные данные в выходные данные в соответствии с бизнес-логикой.
 * </p>
 */
public interface PilotFlightService {

    /**
     * Обрабатывает входные данные о пилотах и рейсах и возвращает результат обработки.
     * <p>
     * Метод выполняет обработку данных, полученных из объекта {@link InputPilotsAndFlights}, и
     * возвращает объект {@link OutputPilotsAndFlights}, который содержит результат обработки.
     * </p>
     *
     * @param inputPilotsAndFlights Объект, содержащий входные данные о пилотах и рейсах.
     * @return Обработанные данные о пилотах и рейсах в виде объекта {@link OutputPilotsAndFlights}.
     */
    OutputPilotsAndFlights process(InputPilotsAndFlights inputPilotsAndFlights);
}
