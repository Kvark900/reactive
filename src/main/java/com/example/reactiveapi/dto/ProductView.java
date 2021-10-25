package com.example.reactiveapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.StringJoiner;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductView {
    private int id;
    private List<Double> prices;

    @Override
    public String toString() {
        return new StringJoiner(", ",
                "{", "}\n")
                .add("id: " + id)
                .add("prices: " + prices)
                .add("")
                .toString();
    }
}
