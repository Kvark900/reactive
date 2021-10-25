package com.example.reactiveapi.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.StringJoiner;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table
public class Product {

    @Id
    private Long id;

    private Integer product_id;
    private Double price;

}