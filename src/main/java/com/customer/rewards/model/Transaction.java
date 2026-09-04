package com.customer.rewards.model;

import com.customer.rewards.converter.LocalDateConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Convert;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "customer_id")
    private String customerId;

    @Convert(converter = LocalDateConverter.class)
    @Column(name = "transaction_date")
    private LocalDate transactionDate;

    @Column(name = "amount")
    private BigDecimal amount;
}