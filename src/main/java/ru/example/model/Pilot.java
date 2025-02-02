package ru.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * Модель, представляющая информацию о пилоте.
 * <p>
 * Этот класс содержит данные о пилоте, включая уникальный идентификатор, полное имя и список месяцев,
 * в которых пилот выполнял полеты (представленных объектами {@link TimeMonth}).
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "idPilot")
public class Pilot {

    /**
     * Уникальный идентификатор пилота.
     */
    private Long idPilot;

    /**
     * Полное имя пилота.
     */
    private String fullName;

    /**
     * Список объектов {@link TimeMonth}, представляющих информацию о времени, проведенном пилотом
     * в каждом месяце.
     */
    private List<TimeMonth> timeMonthList = new ArrayList<>();
}