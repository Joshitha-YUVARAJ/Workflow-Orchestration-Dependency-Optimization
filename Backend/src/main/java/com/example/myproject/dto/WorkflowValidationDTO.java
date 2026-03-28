package com.example.myproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WorkflowValidationDTO {
    private boolean valid;
    private String message;
}
