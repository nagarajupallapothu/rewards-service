package com.customer.rewards.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class TransactionReward {

    private String transactionId;

    private LocalDate transactionDate;

    private BigDecimal amount;

    private int rewardPoints;
}