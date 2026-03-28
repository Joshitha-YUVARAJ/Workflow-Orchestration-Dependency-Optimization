package com.example.myproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;


@Getter
@AllArgsConstructor
public class ExecutionOrderDTO
{
        private Long workflowId;
        private String message;
        private List<Long> orderedTaskIds;

}

