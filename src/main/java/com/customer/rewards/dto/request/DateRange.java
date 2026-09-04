package com.customer.rewards.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class DateRange {

    private LocalDate fromDate;

    private LocalDate toDate;
}