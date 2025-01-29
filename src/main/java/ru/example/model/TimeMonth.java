package ru.example.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TimeMonth {
    @JsonFormat(pattern = "yyyy LLLL", locale = "ru")
    private LocalDate date;
//    private Long totalFlights;
    private Long totalFlightHours;
    private boolean exceedsMonthlyLimit;
    private boolean exceedsWeeklyLimit;
    private boolean exceedsDailyLimit;

    public TimeMonth(LocalDate date, Long totalFlightHours) {
        this.date = date;
        this.totalFlightHours = totalFlightHours;
        setFlightLimitsFlags();
    }

    private void setFlightLimitsFlags() {
        this.exceedsMonthlyLimit = this.totalFlightHours > 80;
        this.exceedsWeeklyLimit = this.totalFlightHours > 36;
        this.exceedsDailyLimit = this.totalFlightHours > 8;
    }
}
