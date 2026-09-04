package com.customer.rewards.service;

import com.customer.rewards.dto.request.DateRange;
import com.customer.rewards.dto.response.MonthlyReward;
import com.customer.rewards.dto.response.RewardResponse;
import com.customer.rewards.dto.response.RewardSummary;
import com.customer.rewards.dto.response.TransactionReward;
import com.customer.rewards.dto.response.CustomerInfo;
import com.customer.rewards.exception.CustomerNotFoundException;
import com.customer.rewards.model.Customer;
import com.customer.rewards.model.Transaction;
import com.customer.rewards.repository.CustomerRepository;
import com.customer.rewards.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RewardService {

    private final CustomerRepository customerRepository;

    private final TransactionRepository transactionRepository;

    private final RewardCalculator rewardCalculator;

    private final DateRangeResolver dateRangeResolver;

    public RewardResponse calculateRewards(
            String customerId,
            LocalDate fromDate,
            LocalDate toDate) {

        log.info(
                "Starting reward calculation. customerId={}, fromDate={}, toDate={}",
                customerId,
                fromDate,
                toDate
        );

        DateRange dateRange =
                dateRangeResolver.resolve(
                        fromDate,
                        toDate
                );

        fromDate =
                dateRange.getFromDate();

        toDate =
                dateRange.getToDate();

        log.info(
                "Starting reward calculation. customerId={}, fromDate={}, toDate={}",
                customerId,
                fromDate,
                toDate
        );


        Customer customer =
                customerRepository.findById(customerId)
                        .orElseThrow(() -> {
                            log.warn(
                                    "Customer not found. customerId={}",
                                    customerId
                            );
                            return new CustomerNotFoundException(
                                    customerId
                            );
                        });

        List<Transaction> transactions =
                transactionRepository
                        .findByCustomerIdAndTransactionDateBetween(
                                customerId,
                                fromDate,
                                toDate
                        );

        log.debug(
                "Retrieved transactions. customerId={}, transactionCount={}",
                customerId,
                transactions.size()
        );

        if (transactions.isEmpty()) {
            log.info(
                    "No transactions found for customer within requested date range. " +
                            "customerId={}, fromDate={}, toDate={}",
                    customerId,
                    fromDate,
                    toDate
            );

        }

        /*
         * Calculate reward points only once for every transaction.
         */
        List<TransactionReward> transactionRewards =
                transactions.stream()
                        .map(this::buildTransactionReward)
                        .toList();

        /*
         * Group already-calculated transaction rewards by month.
         */
        Map<YearMonth, List<TransactionReward>> rewardsByMonth =
                transactionRewards.stream()
                        .collect(Collectors.groupingBy(
                                reward ->
                                        YearMonth.from(
                                                reward.getTransactionDate()
                                        )
                        ));

        /*
         * Build monthly rewards using the already calculated
         * transaction reward points.
         */
        List<MonthlyReward> monthlyRewards =
                rewardsByMonth.entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByKey())
                        .map(entry ->
                                buildMonthlyReward(
                                        entry.getKey(),
                                        entry.getValue()
                                )
                        )
                        .toList();

        /*
         * Build overall summary from the already calculated
         * transaction rewards.
         */
        RewardSummary summary =
                buildSummary(transactions, transactionRewards);

        log.info(
                "Reward calculation completed. customerId={}, totalTransactions={}, totalRewardPoints={}",
                customerId,
                summary.getTotalTransactions(),
                summary.getTotalRewardPoints()
        );

        return RewardResponse.builder()
                .customer(
                        CustomerInfo.builder()
                                .customerId(customer.getCustomerId())
                                .customerName(customer.getCustomerName())
                                .build()
                )
                .fromDate(fromDate)
                .toDate(toDate)
                .monthlyRewards(monthlyRewards)
                .summary(summary)
                .build();
    }

    /**
     * Calculates reward points exactly once for a transaction.
     */
    private TransactionReward buildTransactionReward(
            Transaction transaction) {

        int rewardPoints =
                rewardCalculator.calculate(
                        transaction.getAmount()
                );

        return TransactionReward.builder()
                .transactionId(
                        transaction.getTransactionId()
                )
                .transactionDate(
                        transaction.getTransactionDate()
                )
                .amount(
                        transaction.getAmount()
                )
                .rewardPoints(rewardPoints)
                .build();
    }

    /**
     * Builds monthly reward using already calculated
     * TransactionReward objects.
     */
    private MonthlyReward buildMonthlyReward(
            YearMonth month,
            List<TransactionReward> transactionRewards) {

        BigDecimal totalSpent =
                transactionRewards.stream()
                        .map(TransactionReward::getAmount)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        int totalRewardPoints =
                transactionRewards.stream()
                        .mapToInt(
                                TransactionReward::getRewardPoints
                        )
                        .sum();

        return MonthlyReward.builder()
                .month(month.toString())
                .transactions(
                        transactionRewards.stream()
                                .sorted(
                                        (r1, r2) ->
                                                r1.getTransactionDate()
                                                        .compareTo(
                                                                r2.getTransactionDate()
                                                        )
                                )
                                .toList()
                )
                .totalSpent(totalSpent)
                .totalRewardPoints(totalRewardPoints)
                .build();
    }

    /**
     * Builds overall summary from the original transactions
     * and already calculated reward points.
     */
    private RewardSummary buildSummary(
            List<Transaction> transactions,
            List<TransactionReward> transactionRewards) {

        BigDecimal totalSpent =
                transactions.stream()
                        .map(Transaction::getAmount)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        int totalRewardPoints =
                transactionRewards.stream()
                        .mapToInt(TransactionReward::getRewardPoints)
                        .sum();

        return RewardSummary.builder()
                .totalTransactions(transactions.size())
                .totalSpent(totalSpent)
                .totalRewardPoints(totalRewardPoints)
                .build();
    }
}