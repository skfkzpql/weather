package com.hyunn.weather;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WeatherApplicationTests {

    @Test
    void equalTest() {
        int a = 1;
        int b = 1;
        assertEquals(a, b);
    }

    @Test
    void nullTest() {
        assertNull(null);
    }

    @Test
    void trueTest() {
        int a = 1;
        int b = 1;
        assertTrue(a == 1);
    }
}
