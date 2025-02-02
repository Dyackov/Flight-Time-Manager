package ru.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.example.model.Flight;
import ru.example.dto.InputPilotsAndFlights;
import ru.example.dto.OutputPilotsAndFlights;
import ru.example.model.Pilot;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class PilotFlightServiceImplTest {

    private PilotFlightServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PilotFlightServiceImpl();
    }

    @Test
    void process_ValidPilotAndFlight_PilotPresentInOutput() {
        Pilot pilot = new Pilot(1L, "Иван Иванов", null);
        Flight flight = new Flight(1L, "Boeing 737", "ABC123",
                LocalDateTime.of(2023, 10, 1, 10, 0),
                LocalDateTime.of(2023, 10, 1, 12, 0),
                "SVO", "LED", List.of(1L));

        InputPilotsAndFlights input = new InputPilotsAndFlights(Set.of(pilot), Set.of(flight));
        OutputPilotsAndFlights output = service.process(input);

        List<Pilot> outSpecialist = output.getSpecialists();

        assertEquals(1, outSpecialist.size());
        assertTrue(outSpecialist.contains(pilot));
    }

    @Test
    void process_ValidPilotAndFlightWithExceededDailyLimit_FlagsCorrectly() {
        Pilot invalidPilot = new Pilot(1L, "Иван Иванов", null);
        Flight flight = new Flight(1L, "Boeing 737", "ABC123",
                LocalDateTime.of(2023, 10, 1, 10, 0),
                LocalDateTime.of(2023, 10, 1, 19, 0),
                "SVO", "LED", List.of(1L));

        InputPilotsAndFlights input = new InputPilotsAndFlights(Set.of(invalidPilot), Set.of(flight));
        OutputPilotsAndFlights output = service.process(input);
        List<Pilot> outSpecialist = output.getSpecialists();

        assertEquals(1, outSpecialist.getFirst().getTimeMonthList().size());
        assertTrue(outSpecialist.getFirst().getTimeMonthList().getFirst().isExceedsDailyLimit());
    }

    @Test
    void process_ValidPilotAndFlightWithExceedsWeeklyLimitFlagsCorrectly() {
        Pilot invalidPilot = new Pilot(1L, "Иван Иванов", null);
        Flight flight = new Flight(1L, "Boeing 737", "ABC123",
                LocalDateTime.of(2025, 2, 4, 12, 0),
                LocalDateTime.of(2025, 2, 7, 12, 0),
                "SVO", "LED", List.of(1L));

        InputPilotsAndFlights input = new InputPilotsAndFlights(Set.of(invalidPilot), Set.of(flight));
        OutputPilotsAndFlights output = service.process(input);
        List<Pilot> outSpecialist = output.getSpecialists();

        assertEquals(1, outSpecialist.getFirst().getTimeMonthList().size());
        assertTrue(outSpecialist.getFirst().getTimeMonthList().getFirst().isExceedsWeeklyLimit());
    }

    @Test
    void process_ValidPilotAndFlightWithExceedsMonthlyLimitFlagsCorrectly() {
        Pilot invalidPilot = new Pilot(1L, "Иван Иванов", null);
        Flight flight = new Flight(1L, "Boeing 737", "ABC123",
                LocalDateTime.of(2025, 2, 4, 12, 0),
                LocalDateTime.of(2025, 2, 15, 12, 0),
                "SVO", "LED", List.of(1L));

        InputPilotsAndFlights input = new InputPilotsAndFlights(Set.of(invalidPilot), Set.of(flight));
        OutputPilotsAndFlights output = service.process(input);
        List<Pilot> outSpecialist = output.getSpecialists();

        assertEquals(1, outSpecialist.getFirst().getTimeMonthList().size());
        assertTrue(outSpecialist.getFirst().getTimeMonthList().getFirst().isExceedsMonthlyLimit());
    }

    @Test
    void process_FlightsSpanningMonthBoundary_WeeklyLimitFlagCorrectlySet() {
        Pilot invalidPilot = new Pilot(1L, "Иван Иванов", null);
        Flight flight = new Flight(1L, "Boeing 737", "ABC123",
                LocalDateTime.of(2025, 1, 30, 1, 0),
                LocalDateTime.of(2025, 2, 1, 12, 0),
                "SVO", "LED", List.of(1L));

        InputPilotsAndFlights input = new InputPilotsAndFlights(Set.of(invalidPilot), Set.of(flight));
        OutputPilotsAndFlights output = service.process(input);
        List<Pilot> outSpecialist = output.getSpecialists();

        assertEquals(2, outSpecialist.getFirst().getTimeMonthList().size());
        assertTrue(outSpecialist.getFirst().getTimeMonthList().getFirst().isExceedsWeeklyLimit());
    }

    @Test
    void process_MultipleFlightsWithLongDuration_CalculatesTotalFlightHoursCorrectly() {
        Pilot invalidPilot = new Pilot(1L, "Иван Иванов", null);
        Flight flight1 = new Flight(1L, "Boeing 737", "ABC123",
                LocalDateTime.of(2025, 1, 5, 1, 0),
                LocalDateTime.of(2025, 1, 7, 12, 0),
                "SVO", "LED", List.of(1L));

        Flight flight2 = new Flight(2L, "Boeing 312", "ABC323",
                LocalDateTime.of(2025, 1, 10, 10, 0),
                LocalDateTime.of(2025, 1, 12, 3, 0),
                "SVO", "LED", List.of(1L));

        Flight flight3 = new Flight(3L, "Boeing 937", "ABC4123",
                LocalDateTime.of(2025, 1, 25, 15, 0),
                LocalDateTime.of(2025, 1, 29, 10, 0),
                "SVO", "LED", List.of(1L));

        InputPilotsAndFlights input = new InputPilotsAndFlights(Set.of(invalidPilot), Set.of(flight1,flight2,flight3));
        OutputPilotsAndFlights output = service.process(input);
        List<Pilot> outSpecialist = output.getSpecialists();

        assertEquals(1, outSpecialist.getFirst().getTimeMonthList().size());
        assertEquals(191, outSpecialist.getFirst().getTimeMonthList().getFirst().getTotalFlightHours());
    }

    @Test
    void process_MultipleFlightsWithLongDuration_CalculatesTotalFlights() {
        Pilot invalidPilot = new Pilot(1L, "Иван Иванов", null);
        Flight flight1 = new Flight(1L, "Boeing 737", "ABC123",
                LocalDateTime.of(2025, 1, 5, 1, 0),
                LocalDateTime.of(2025, 1, 7, 12, 0),
                "SVO", "LED", List.of(1L));

        Flight flight2 = new Flight(2L, "Boeing 312", "ABC323",
                LocalDateTime.of(2025, 1, 10, 10, 0),
                LocalDateTime.of(2025, 1, 12, 3, 0),
                "SVO", "LED", List.of(1L));

        Flight flight3 = new Flight(3L, "Boeing 937", "ABC4123",
                LocalDateTime.of(2025, 1, 25, 15, 0),
                LocalDateTime.of(2025, 1, 29, 10, 0),
                "SVO", "LED", List.of(1L));

        InputPilotsAndFlights input = new InputPilotsAndFlights(Set.of(invalidPilot), Set.of(flight1,flight2,flight3));
        OutputPilotsAndFlights output = service.process(input);
        List<Pilot> outSpecialist = output.getSpecialists();

        assertEquals(3, outSpecialist.getFirst().getTimeMonthList().getFirst().getTotalFlightsInMonth());
    }
}