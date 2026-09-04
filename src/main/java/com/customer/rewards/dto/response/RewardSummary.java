package com.customer.rewards.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class RewardSummary {

    private int totalTransactions;

    private BigDecimal totalSpent;

    private int totalRewardPoints;
}