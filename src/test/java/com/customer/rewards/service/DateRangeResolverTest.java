package com.customer.rewards.service;

import com.customer.rewards.dto.request.DateRange;
import com.customer.rewards.exception.InvalidDateRangeException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DateRangeResolverTest {

    private final DateRangeResolver resolver =
            new DateRangeResolver();

    @Test
    void shouldUsePreviousThreeMonthsWhenNoDatesProvided() {

        LocalDate today = LocalDate.now();

        DateRange result =
                resolver.resolve(null, null);

        assertEquals(
                today.minusMonths(3),
                result.getFromDate()
        );

        assertEquals(
                today,
                result.getToDate()
        );
    }

    @Test
    void shouldUseFromDateToTodayWhenOnlyFromDateProvided() {

        LocalDate fromDate =
                LocalDate.of(2026, 7, 1);

        LocalDate today =
                LocalDate.now();

        DateRange result =
                resolver.resolve(fromDate, null);

        assertEquals(
                fromDate,
                result.getFromDate()
        );

        assertEquals(
                today,
                result.getToDate()
        );
    }

    @Test
    void shouldUseThreeMonthsBeforeToDateWhenOnlyToDateProvided() {

        LocalDate toDate =
                LocalDate.of(2026, 8, 31);

        DateRange result =
                resolver.resolve(null, toDate);

        assertEquals(
                toDate.minusMonths(3),
                result.getFromDate()
        );

        assertEquals(
                toDate,
                result.getToDate()
        );
    }

    @Test
    void shouldUseProvidedDatesWhenBothDatesProvided() {

        LocalDate fromDate =
                LocalDate.of(2026, 6, 1);

        LocalDate toDate =
                LocalDate.of(2026, 8, 31);

        DateRange result =
                resolver.resolve(fromDate, toDate);

        assertEquals(
                fromDate,
                result.getFromDate()
        );

        assertEquals(
                toDate,
                result.getToDate()
        );
    }

    @Test
    void shouldRejectWhenFromDateIsAfterToDate() {

        LocalDate fromDate =
                LocalDate.of(2026, 8, 31);

        LocalDate toDate =
                LocalDate.of(2026, 6, 1);

        assertThrows(
                InvalidDateRangeException.class,
                () -> resolver.resolve(
                        fromDate,
                        toDate
                )
        );
    }
}