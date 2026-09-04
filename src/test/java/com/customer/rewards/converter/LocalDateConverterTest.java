package com.customer.rewards.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


class LocalDateConverterTest {

    private LocalDateConverter converter;

    @BeforeEach
    void setUp() {
        converter = new LocalDateConverter();
    }

    @Test
    void shouldConvertLocalDateToDatabaseString() {

        // Given
        LocalDate date = LocalDate.of(2026, 6, 15);

        // When
        String result =
                converter.convertToDatabaseColumn(date);

        // Then
        assertEquals("2026-06-15", result);
    }

    @Test
    void shouldReturnNullWhenLocalDateIsNull() {

        // When
        String result =
                converter.convertToDatabaseColumn(null);

        // Then
        assertNull(result);
    }

    @Test
    void shouldConvertDatabaseStringToLocalDate() {

        // Given
        String databaseValue = "2026-06-15";

        // When
        LocalDate result =
                converter.convertToEntityAttribute(databaseValue);

        // Then
        assertEquals(
                LocalDate.of(2026, 6, 15),
                result
        );
    }

    @Test
    void shouldReturnNullWhenDatabaseValueIsNull() {

        // When
        LocalDate result =
                converter.convertToEntityAttribute(null);

        // Then
        assertNull(result);
    }

    @Test
    void shouldPreserveDateDuringRoundTripConversion() {

        // Given
        LocalDate originalDate =
                LocalDate.of(2026, 8, 31);

        // When
        String databaseValue =
                converter.convertToDatabaseColumn(originalDate);

        LocalDate result =
                converter.convertToEntityAttribute(databaseValue);

        // Then
        assertEquals(originalDate, result);
    }
}
