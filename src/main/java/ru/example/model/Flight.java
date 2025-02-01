package ru.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Модель, представляющая информацию о рейсе.
 * <p>
 * Этот класс содержит данные о рейсе, включая информацию о типе и номере воздушного судна,
 * времени отправления и прибытия, а также аэропортах отправления и прибытия. Также включен список
 * идентификаторов пилотов, участвующих в рейсе.
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Flight {

    /**
     * Уникальный идентификатор рейса.
     */
    private Long id;

    /**
     * Тип воздушного судна.
     */
    private String aircraftType;

    /**
     * Номер воздушного судна.
     */
    private String aircraftNumber;

    /**
     * Время отправления рейса.
     */
    private LocalDateTime departureTime;

    /**
     * Время прибытия рейса.
     */
    private LocalDateTime arrivalTime;

    /**
     * Аэропорт отправления рейса.
     */
    private String departureAirport;

    /**
     * Аэропорт прибытия рейса.
     */
    private String arrivalAirport;

    /**
     * Список идентификаторов пилотов, участвующих в рейсе.
     */
    private List<Long> idPilots;
}
