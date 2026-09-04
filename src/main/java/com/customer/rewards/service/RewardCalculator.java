package com.customer.rewards.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class RewardCalculator {

    private static final BigDecimal FIFTY =
            BigDecimal.valueOf(50);

    private static final BigDecimal HUNDRED =
            BigDecimal.valueOf(100);

    private static final int TWO_POINTS = 2;

    public int calculate(BigDecimal amount) {

        if (amount == null ||
                amount.compareTo(FIFTY) <= 0) {

            return 0;
        }

        if (amount.compareTo(HUNDRED) <= 0) {

            return amount
                    .subtract(FIFTY)
                    .intValue();
        }

        return FIFTY.intValue()
                + amount
                    .subtract(HUNDRED)
                    .intValue() * TWO_POINTS;
    }
}