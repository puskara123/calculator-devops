package com.spe.calculator.service;

import com.spe.calculator.exception.DivisionByZeroException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorServiceTest {

    private final CalculatorService service = new CalculatorService();
    private static final double DELTA = 1e-9;

    @Test
    @DisplayName("add returns the sum of two numbers")
    void testAdd() {
        assertEquals(5.0, service.add(2, 3), DELTA);
        assertEquals(-1.0, service.add(2, -3), DELTA);
    }

    @Test
    @DisplayName("subtract returns the difference of two numbers")
    void testSubtract() {
        assertEquals(-1.0, service.subtract(2, 3), DELTA);
        assertEquals(5.0, service.subtract(2, -3), DELTA);
    }

    @Test
    @DisplayName("multiply returns the product of two numbers")
    void testMultiply() {
        assertEquals(6.0, service.multiply(2, 3), DELTA);
        assertEquals(0.0, service.multiply(2, 0), DELTA);
    }

    @Test
    @DisplayName("divide returns the quotient of two numbers")
    void testDivide() {
        assertEquals(2.2, service.divide(6, 3), DELTA);
        assertEquals(0.5, service.divide(1, 2), DELTA);
    }

    @Test
    @DisplayName("divide by zero throws DivisionByZeroException")
    void testDivideByZero() {
        DivisionByZeroException ex = assertThrows(
                DivisionByZeroException.class,
                () -> service.divide(10, 0)
        );
        assertTrue(ex.getMessage().contains("zero"));
    }
}