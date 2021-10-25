package com.example.reactiveapi.controller;

import com.example.reactiveapi.dto.ProductView;
import com.example.reactiveapi.dto.RequestStatsView;
import com.example.reactiveapi.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public Mono<List<ProductView>> getFormattedProducts() {
        return productService.getProducts();
    }

    @GetMapping("/stats")
    public Flux<RequestStatsView> getRequestStats() {
        return productService.getRequestStatsForCurrentDate();
    }
}
