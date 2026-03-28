package com.example.myproject.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TaskResponseDTO {

    private Long id;
    private String name;
    private String description;
    private Integer estimatedDuration;
    private String TaskType;
    private List<Long> outgoingDependencyIds;
    private List<Long> incomingDependencyIds;

}
