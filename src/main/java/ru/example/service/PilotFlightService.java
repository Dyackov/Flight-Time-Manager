package ru.example.service;

import ru.example.model.InputPilotsAndFlights;
import ru.example.model.OutputPilotsAndFlights;

public interface PilotFlightService {
    OutputPilotsAndFlights process(InputPilotsAndFlights inputPilotsAndFlights);
}