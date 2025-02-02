package ru.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.example.model.Pilot;

import java.util.ArrayList;
import java.util.List;

/**
 * Модель, представляющая выходные данные для пилотов и рейсов.
 * <p>
 * Этот класс используется для хранения списка пилотов, которые были обработаны
 * и подготовлены для вывода после обработки данных о рейсах и пилотах.
 * </p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OutputPilotsAndFlights {

    /**
     * Список пилотов, которые прошли обработку и будут включены в выходные данные.
     */
    private List<Pilot> specialists = new ArrayList<>();
}
