package ru.example.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
public class TimeMonth {
    @JsonFormat(pattern = "yyyy LLLL", locale = "ru")
    private LocalDate date;
    private Long totalFlightHours;
    private boolean exceedsMonthlyLimit;
    private boolean exceedsWeeklyLimit;
    private boolean exceedsDailyLimit;

//    public TimeMonth(LocalDate date, Long totalFlightHours) {
//        this.date = date;
//        this.totalFlightHours = totalFlightHours;
//    }
////
//    private void setFlightLimitsFlags() {
//        this.exceedsMonthlyLimit = this.totalFlightHours > 80;
////        this.exceedsWeeklyLimit = this.totalFlightHours > 36;
////        this.exceedsDailyLimit = this.totalFlightHours > 8;
//    }
//
//    private void setFlightLimitsFlags22222(Map<LocalDate,Long> hoursPerDay) {
//        Map<LocalDate,Long> hhhh = new HashMap<>();
//
//        for (Map.Entry<LocalDate,Long> entry : hoursPerDay.entrySet()) {
//
//        }
//    }
}
