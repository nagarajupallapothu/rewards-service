package com.customer.rewards.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class MonthlyReward {

    private String month;

    private List<TransactionReward> transactions;

    private BigDecimal totalSpent;

    private int totalRewardPoints;
}