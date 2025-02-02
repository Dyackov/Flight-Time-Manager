package ru.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.example.model.Flight;
import ru.example.model.Pilot;

import java.util.Set;

/**
 * Модель, представляющая входные данные для пилотов и рейсов.
 * <p>
 * Этот класс содержит два набора: один для пилотов {@link Pilot},
 * и второй для рейсов {@link Flight}, которые будут использоваться
 * для обработки и взаимодействия в системе.
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InputPilotsAndFlights {

    /**
     * Набор пилотов, участвующих в рейсах.
     */
    private Set<Pilot> pilots;

    /**
     * Набор рейсов, с которыми связаны пилоты.
     */
    private Set<Flight> flights;
}
