package com.customer.rewards.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class RewardResponse {

    private CustomerInfo customer;

    private LocalDate fromDate;

    private LocalDate toDate;

    private List<MonthlyReward> monthlyRewards;

    private RewardSummary summary;
}