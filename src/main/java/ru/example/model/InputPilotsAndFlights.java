package ru.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InputPilotsAndFlights {
    private Set<Pilot> pilots;
    private Set<Flight> flights;
}
