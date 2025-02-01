package ru.example.app;

import lombok.extern.slf4j.Slf4j;
import ru.example.config.ObjectMapperConfig;
import ru.example.model.InputPilotsAndFlights;
import ru.example.model.OutputPilotsAndFlights;
import ru.example.service.DataPersistenceService;
import ru.example.service.DataPersistenceServiceImpl;
import ru.example.service.PilotFlightService;
import ru.example.service.PilotFlightServiceImpl;

import java.io.File;

@Slf4j
public class PilotServiceRunner {
    private final File inputFile = new File("src/main/resources/InputPilotsAndFlights.json");
    private final File outputFile = new File("src/main/resources/OutputPilotsAndFlights.json");

    private final DataPersistenceService dataPersistenceServiceImpl;
    private final PilotFlightService pilotFlightServiceImpl;


    public PilotServiceRunner() {
        this.dataPersistenceServiceImpl = new DataPersistenceServiceImpl(ObjectMapperConfig.createConfigObjectMapperTime());
        this.pilotFlightServiceImpl = new PilotFlightServiceImpl();
    }

    public void run(){
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