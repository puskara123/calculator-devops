package com.spe.calculator.controller;

import com.spe.calculator.model.CalculationResponse;
import com.spe.calculator.service.CalculatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class CalculatorController {

    private final CalculatorService service;

    public CalculatorController(CalculatorService service) {
        this.service = service;
    }

    @GetMapping("/add")
    public ResponseEntity<CalculationResponse> add(@RequestParam double a, @RequestParam double b) {
        return ResponseEntity.ok(new CalculationResponse("add", a, b, service.add(a, b)));
    }

    @GetMapping("/subtract")
    public ResponseEntity<CalculationResponse> subtract(@RequestParam double a, @RequestParam double b) {
        return ResponseEntity.ok(new CalculationResponse("subtract", a, b, service.subtract(a, b)));
    }

    @GetMapping("/multiply")
    public ResponseEntity<CalculationResponse> multiply(@RequestParam double a, @RequestParam double b) {
        return ResponseEntity.ok(new CalculationResponse("multiply", a, b, service.multiply(a, b)));
    }

    @GetMapping("/divide")
    public ResponseEntity<CalculationResponse> divide(@RequestParam double a, @RequestParam double b) {
        return ResponseEntity.ok(new CalculationResponse("divide", a, b, service.divide(a, b)));
    }
}