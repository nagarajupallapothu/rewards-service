package com.customer.rewards.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerInfo {

    private String customerId;

    private String customerName;
}