package ru.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "idPilot")
public class Pilot {
    private Long idPilot;
    private String fullName;
    private List<TimeMonth> timeMonthList = new ArrayList<>();
}