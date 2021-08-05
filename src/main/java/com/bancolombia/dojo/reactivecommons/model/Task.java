package com.bancolombia.dojo.reactivecommons.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Task {
    private String name;
    private String description;
    private String supervisor;
}
