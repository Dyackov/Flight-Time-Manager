package ru.example.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TimeMonth {
    @JsonFormat(pattern = "yyyy LLLL", locale = "ru")
    private LocalDate date;
    private Long totalFlightHours;
    private Long totalFlightsInMonth;
    private boolean exceedsMonthlyLimit;
    private boolean exceedsWeeklyLimit;
    private boolean exceedsDailyLimit;
}