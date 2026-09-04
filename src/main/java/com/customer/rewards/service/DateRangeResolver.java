package com.customer.rewards.service;

import com.customer.rewards.dto.request.DateRange;
import com.customer.rewards.exception.InvalidDateRangeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Slf4j
public class DateRangeResolver {

    private static final int DEFAULT_MONTHS = 3;

    public DateRange resolve(
            LocalDate fromDate,
            LocalDate toDate) {

        LocalDate effectiveFromDate;
        LocalDate effectiveToDate;

        /*
         * Case 1:
         * Neither date is provided.
         *
         * fromDate = today - 3 months
         * toDate   = today
         */
        if (fromDate == null && toDate == null) {

            effectiveToDate = LocalDate.now();

            effectiveFromDate =
                    effectiveToDate.minusMonths(DEFAULT_MONTHS);

            log.debug(
                    "No dates provided. Using default date range. " +
                    "fromDate={}, toDate={}",
                    effectiveFromDate,
                    effectiveToDate
            );

            return new DateRange(
                    effectiveFromDate,
                    effectiveToDate
            );
        }

        /*
         * Case 2:
         * Only fromDate is provided.
         *
         * fromDate = supplied fromDate
         * toDate   = today
         */
        if (fromDate != null && toDate == null) {

            effectiveFromDate = fromDate;
            effectiveToDate = LocalDate.now();

            validateDateRange(
                    effectiveFromDate,
                    effectiveToDate
            );

            log.debug(
                    "Only fromDate provided. Using fromDate to today. " +
                    "fromDate={}, toDate={}",
                    effectiveFromDate,
                    effectiveToDate
            );

            return new DateRange(
                    effectiveFromDate,
                    effectiveToDate
            );
        }

        /*
         * Case 3:
         * Only toDate is provided.
         *
         * fromDate = toDate - 3 months
         * toDate   = supplied toDate
         */
        if (fromDate == null) {

            effectiveToDate = toDate;

            effectiveFromDate =
                    effectiveToDate.minusMonths(DEFAULT_MONTHS);

            log.debug(
                    "Only toDate provided. Using three months prior to toDate. " +
                    "fromDate={}, toDate={}",
                    effectiveFromDate,
                    effectiveToDate
            );

            return new DateRange(
                    effectiveFromDate,
                    effectiveToDate
            );
        }

        /*
         * Case 4:
         * Both dates are provided.
         *
         * Use exactly the supplied date range.
         */
        validateDateRange(
                fromDate,
                toDate
        );

        log.debug(
                "Both dates provided. Using supplied date range. " +
                "fromDate={}, toDate={}",
                fromDate,
                toDate
        );

        return new DateRange(
                fromDate,
                toDate
        );
    }

    private void validateDateRange(
            LocalDate fromDate,
            LocalDate toDate) {

        if (fromDate.isAfter(toDate)) {

            log.warn(
                    "Invalid date range. fromDate={}, toDate={}",
                    fromDate,
                    toDate
            );

            throw new InvalidDateRangeException(
                    "fromDate must be before or equal to toDate"
            );
        }
    }
}