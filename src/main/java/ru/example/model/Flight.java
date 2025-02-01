package ru.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Flight {
    private Long id;
    private String aircraftType;
    private String aircraftNumber;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String departureAirport;
    private String arrivalAirport;
    private List<Long> idPilots;
}