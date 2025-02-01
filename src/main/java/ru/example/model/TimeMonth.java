package ru.example.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

/**
 * Модель, представляющая информацию о времени и полетах пилота за месяц.
 * <p>
 * Этот класс содержит данные о времени, проведенном пилотом в определенном месяце,
 * а также информацию о количестве полетов и нарушении различных лимитов (ежемесячных, еженедельных, ежедневных).
 * </p>
 */
@Data
public class TimeMonth {

    /**
     * Дата, представляющая месяц в формате "yyyy LLLL" (например, "2025 Январь").
     * Используется аннотация {@link JsonFormat} для задания формата даты и локали.
     */
    @JsonFormat(pattern = "yyyy LLLL", locale = "ru")
    private LocalDate date;

    /**
     * Общее количество часов полетов в указанном месяце.
     */
    private Long totalFlightHours;

    /**
     * Общее количество полетов, совершенных в указанном месяце.
     */
    private Long totalFlightsInMonth;

    /**
     * Флаг, указывающий, превышает ли пилот ежемесячный лимит полетов.
     */
    private boolean exceedsMonthlyLimit;

    /**
     * Флаг, указывающий, превышает ли пилот недельный лимит полетов.
     */
    private boolean exceedsWeeklyLimit;

    /**
     * Флаг, указывающий, превышает ли пилот ежедневный лимит полетов.
     */
    private boolean exceedsDailyLimit;
}
