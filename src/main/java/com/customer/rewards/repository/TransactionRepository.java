package com.customer.rewards.repository;
import com.customer.rewards.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository
        extends JpaRepository<Transaction, String> {

    List<Transaction> findByCustomerIdAndTransactionDateBetween(
            String customerId,
            LocalDate fromDate,
            LocalDate toDate);
}