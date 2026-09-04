package com.customer.rewards.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalDate;

@Converter
public class LocalDateConverter
        implements AttributeConverter<LocalDate, String> {

    @Override
    public String convertToDatabaseColumn(
            LocalDate attribute) {

        return attribute != null
                ? attribute.toString()
                : null;
    }

    @Override
    public LocalDate convertToEntityAttribute(
            String databaseValue) {

        return databaseValue != null
                ? LocalDate.parse(databaseValue)
                : null;
    }
}