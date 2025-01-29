package ru.example.app;

import ru.example.config.ObjectMapperConfig;
import ru.example.model.InputPilotsAndFlights;
import ru.example.model.OutputPilotsAndFlights;
import ru.example.service.DataPersistenceService;
import ru.example.service.DataPersistenceServiceImpl;
import ru.example.service.PilotFlightService;
import ru.example.service.PilotFlightServiceImpl;

import java.io.File;

public class Start {
    File inputFile = new File("src/main/resources/InputPilotsAndFlights.json");
    File outputFile = new File("src/main/resources/OutputPilotsAndFlights.json");

    public void pilots(){
        try {
            DataPersistenceService dataPersistenceServiceImpl = new DataPersistenceServiceImpl(ObjectMapperConfig.createConfigObjectMapperTime());
            // Чтение
            InputPilotsAndFlights inputPilotsAndFlights = dataPersistenceServiceImpl.readFile(inputFile, InputPilotsAndFlights.class);

            // Обработка данных
            PilotFlightService pilotFlightServiceImpl = new PilotFlightServiceImpl();
            OutputPilotsAndFlights outputPilotsAndFlights = pilotFlightServiceImpl.process(inputPilotsAndFlights);

            // Запись выходного JSON
            dataPersistenceServiceImpl.writeFile(outputFile, outputPilotsAndFlights);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
