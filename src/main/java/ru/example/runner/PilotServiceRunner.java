package ru.example.runner;

import lombok.extern.slf4j.Slf4j;
import ru.example.config.ObjectMapperConfig;
import ru.example.dto.InputPilotsAndFlights;
import ru.example.dto.OutputPilotsAndFlights;
import ru.example.service.DataPersistenceService;
import ru.example.service.DataPersistenceServiceImpl;
import ru.example.service.PilotFlightService;
import ru.example.service.PilotFlightServiceImpl;

import java.io.File;

/**
 * Класс для обработки данных о пилотах и рейсах.
 * <p>
 * Этот класс читает данные о пилотах и рейсах из входного файла, обрабатывает их с помощью сервиса
 * {@link PilotFlightService}, и записывает результат в выходной файл. Использует сервисы для
 * чтения и записи данных {@link DataPersistenceService}.
 * </p>
 *
 * <p>
 * Для конфигурации ObjectMapper используется метод {@link ObjectMapperConfig#createConfigObjectMapperTime()}.
 * </p>
 */
@Slf4j
public class PilotServiceRunner {

    /**
     * Входной файл с данными о пилотах и рейсах в формате JSON.
     */
    private final File inputFile = new File("src/main/resources/InputPilotsAndFlights.json");

    /**
     * Выходной файл для сохранения обработанных данных о пилотах и рейсах в формате JSON.
     */
    private final File outputFile = new File("src/main/resources/OutputPilotsAndFlights.json");

    /**
     * Сервис для чтения и записи данных.
     */
    private final DataPersistenceService dataPersistenceServiceImpl;

    /**
     * Сервис для обработки данных о пилотах и рейсах.
     */
    private final PilotFlightService pilotFlightServiceImpl;

    /**
     * Конструктор для инициализации сервисов.
     * Создает экземпляры сервисов для обработки данных с использованием конфигурации {@link ObjectMapperConfig}.
     */
    public PilotServiceRunner() {
        this.dataPersistenceServiceImpl = new DataPersistenceServiceImpl(ObjectMapperConfig.createConfigObjectMapperTime());
        this.pilotFlightServiceImpl = new PilotFlightServiceImpl();
    }

    /**
     * Метод для выполнения обработки данных.
     * <p>
     * Процесс включает в себя:
     * 1. Чтение данных из входного файла ({@link InputPilotsAndFlights}).
     * 2. Обработку данных с использованием {@link PilotFlightService}.
     * 3. Запись обработанных данных в выходной файл.
     * </p>
     * В случае ошибки процесс записи и обработки прерывается, и в лог выводится сообщение об ошибке.
     */
    public void run() {
        try {
            // Чтение
            InputPilotsAndFlights inputPilotsAndFlights = dataPersistenceServiceImpl.readFile(inputFile, InputPilotsAndFlights.class);

            // Обработка данных
            OutputPilotsAndFlights outputPilotsAndFlights = pilotFlightServiceImpl.process(inputPilotsAndFlights);

            // Запись выходного JSON
            dataPersistenceServiceImpl.writeFile(outputFile, outputPilotsAndFlights);

            log.info("Пилоты и рейсы успешно обработаны и записаны в файл: {}", outputFile.getPath());
        } catch (Exception e) {
            log.error("Произошла ошибка при обработке пилотов и рейсов: ", e);
        }
    }
}
