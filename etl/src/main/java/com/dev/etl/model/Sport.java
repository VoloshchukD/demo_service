package com.dev.etl.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("sports")
public record Sport(
        @Id Integer id,
        String name
) {
}