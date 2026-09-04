package com.customer.rewards.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RewardCalculatorTest {

    private RewardCalculator rewardCalculator;

    @BeforeEach
    void setUp() {
        rewardCalculator = new RewardCalculator();
    }

    @Test
    void shouldReturnZeroWhenAmountIsNull() {

        // Act
        int rewardPoints = rewardCalculator.calculate(null);

        // Assert
        assertEquals(0, rewardPoints);
    }


    @Test
    void shouldReturnZeroWhenAmountIsZero() {

        assertEquals(
                0,
                rewardCalculator.calculate(BigDecimal.ZERO)
        );
    }

    @Test
    void shouldReturnZeroWhenAmountIsFifty() {

        assertEquals(
                0,
                rewardCalculator.calculate(BigDecimal.valueOf(50))
        );
    }

    @Test
    void shouldCalculateOnePointForAmountOfFiftyOne() {

        assertEquals(
                1,
                rewardCalculator.calculate(BigDecimal.valueOf(51))
        );
    }

    @Test
    void shouldCalculateTwentyFivePointsForAmountOfSeventyFive() {

        assertEquals(
                25,
                rewardCalculator.calculate(BigDecimal.valueOf(75))
        );
    }

    @Test
    void shouldCalculateFiftyPointsForAmountOfHundred() {

        assertEquals(
                50,
                rewardCalculator.calculate(BigDecimal.valueOf(100))
        );
    }

    @Test
    void shouldCalculateFiftyTwoPointsForAmountOfHundredAndOne() {

        assertEquals(
                52,
                rewardCalculator.calculate(BigDecimal.valueOf(101))
        );
    }

    @Test
    void shouldCalculateNinetyPointsForAmountOfHundredAndTwenty() {

        assertEquals(
                90,
                rewardCalculator.calculate(BigDecimal.valueOf(120))
        );
    }

    @Test
    void shouldCalculateRewardPointsForLargeAmount() {

        // 200:
        // 50 + (100 * 2) = 250
        assertEquals(
                250,
                rewardCalculator.calculate(BigDecimal.valueOf(200))
        );
    }

    @Test
    void shouldIgnoreDecimalPartBecauseCalculatorUsesIntValue() {

        // 75.99 - 50 = 25.99
        // intValue() => 25
        assertEquals(
                25,
                rewardCalculator.calculate(
                        BigDecimal.valueOf(75.99)
                )
        );
    }
}