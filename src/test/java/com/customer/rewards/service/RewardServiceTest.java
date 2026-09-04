package com.customer.rewards.service;

import com.customer.rewards.dto.request.DateRange;
import com.customer.rewards.dto.response.RewardResponse;
import com.customer.rewards.exception.CustomerNotFoundException;
import com.customer.rewards.model.Customer;
import com.customer.rewards.model.Transaction;
import com.customer.rewards.repository.CustomerRepository;
import com.customer.rewards.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class RewardServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private RewardCalculator rewardCalculator;

    @Mock
    private DateRangeResolver dateRangeResolver;

    @InjectMocks
    private RewardService rewardService;

    private Customer customer;

    @BeforeEach
    void setUp() {

        customer = Customer.builder()
                .customerId("CUST001")
                .customerName("Ravi Kumar")
                .email("ravi@example.com")
                .build();
    }

    @Test
    void shouldCalculateRewardsSuccessfully() {

        // Given
        String customerId = "CUST001";

        LocalDate fromDate =
                LocalDate.of(2026, 6, 1);

        LocalDate toDate =
                LocalDate.of(2026, 8, 31);

        DateRange dateRange =
                new DateRange(fromDate, toDate);

        Transaction transaction1 =
                Transaction.builder()
                        .transactionId("TXN001")
                        .customerId(customerId)
                        .transactionDate(
                                LocalDate.of(2026, 6, 10))
                        .amount(
                                new BigDecimal("120.00"))
                        .build();

        Transaction transaction2 =
                Transaction.builder()
                        .transactionId("TXN002")
                        .customerId(customerId)
                        .transactionDate(
                                LocalDate.of(2026, 7, 15))
                        .amount(
                                new BigDecimal("60.00"))
                        .build();

        Transaction transaction3 =
                Transaction.builder()
                        .transactionId("TXN003")
                        .customerId(customerId)
                        .transactionDate(
                                LocalDate.of(2026, 8, 20))
                        .amount(
                                new BigDecimal("40.00"))
                        .build();

        List<Transaction> transactions =
                List.of(
                        transaction1,
                        transaction2,
                        transaction3
                );

        when(dateRangeResolver.resolve(
                fromDate,
                toDate))
                .thenReturn(dateRange);

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));

        when(transactionRepository
                .findByCustomerIdAndTransactionDateBetween(
                        customerId,
                        fromDate,
                        toDate))
                .thenReturn(transactions);

        when(rewardCalculator.calculate(
                new BigDecimal("120.00")))
                .thenReturn(90);

        when(rewardCalculator.calculate(
                new BigDecimal("60.00")))
                .thenReturn(10);

        when(rewardCalculator.calculate(
                new BigDecimal("40.00")))
                .thenReturn(0);

        // When
        RewardResponse response =
                rewardService.calculateRewards(
                        customerId,
                        fromDate,
                        toDate
                );

        // Then
        assertNotNull(response);

        assertEquals(
                "CUST001",
                response.getCustomer().getCustomerId()
        );

        assertEquals(
                "Ravi Kumar",
                response.getCustomer().getCustomerName()
        );

        assertEquals(
                fromDate,
                response.getFromDate()
        );

        assertEquals(
                toDate,
                response.getToDate()
        );

        assertNotNull(
                response.getMonthlyRewards()
        );

        assertEquals(
                3,
                response.getSummary()
                        .getTotalTransactions()
        );

        assertEquals(
                new BigDecimal("220.00"),
                response.getSummary()
                        .getTotalSpent()
        );

        assertEquals(
                100,
                response.getSummary()
                        .getTotalRewardPoints()
        );

        verify(customerRepository)
                .findById(customerId);

        verify(transactionRepository)
                .findByCustomerIdAndTransactionDateBetween(
                        customerId,
                        fromDate,
                        toDate
                );

        verify(rewardCalculator, times(3))
                .calculate(any(BigDecimal.class));
    }

    @Test
    void shouldThrowCustomerNotFoundExceptionWhenCustomerDoesNotExist() {

        // Given
        String customerId = "INVALID";

        LocalDate fromDate =
                LocalDate.of(2026, 6, 1);

        LocalDate toDate =
                LocalDate.of(2026, 8, 31);

        when(dateRangeResolver.resolve(
                fromDate,
                toDate))
                .thenReturn(
                        new DateRange(
                                fromDate,
                                toDate
                        )
                );

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.empty());

        // When & Then
        CustomerNotFoundException exception =
                assertThrows(
                        CustomerNotFoundException.class,
                        () -> rewardService.calculateRewards(
                                customerId,
                                fromDate,
                                toDate
                        )
                );

        assertEquals(
                "Customer not found: INVALID",
                exception.getMessage()
        );

        verify(customerRepository)
                .findById(customerId);

        verifyNoInteractions(
                transactionRepository,
                rewardCalculator
        );
    }

    @Test
    void shouldReturnZeroRewardsWhenCustomerHasNoTransactions() {

        // Given
        String customerId = "CUST001";

        LocalDate fromDate =
                LocalDate.of(2026, 6, 1);

        LocalDate toDate =
                LocalDate.of(2026, 8, 31);

        DateRange dateRange =
                new DateRange(fromDate, toDate);

        when(dateRangeResolver.resolve(
                fromDate,
                toDate
        )).thenReturn(dateRange);

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));

        when(transactionRepository
                .findByCustomerIdAndTransactionDateBetween(
                        customerId,
                        fromDate,
                        toDate
                ))
                .thenReturn(List.of());

        // When
        RewardResponse response =
                rewardService.calculateRewards(
                        customerId,
                        fromDate,
                        toDate
                );

        // Then
        assertNotNull(response);

        assertEquals(
                "CUST001",
                response.getCustomer().getCustomerId()
        );

        assertEquals(
                fromDate,
                response.getFromDate()
        );

        assertEquals(
                toDate,
                response.getToDate()
        );

        assertNotNull(response.getMonthlyRewards());

        assertTrue(
                response.getMonthlyRewards().isEmpty()
        );

        assertNotNull(response.getSummary());

        assertEquals(
                0,
                response.getSummary().getTotalTransactions()
        );

        assertEquals(
                BigDecimal.ZERO,
                response.getSummary().getTotalSpent()
        );

        assertEquals(
                0,
                response.getSummary().getTotalRewardPoints()
        );

        verify(customerRepository)
                .findById(customerId);

        verify(transactionRepository)
                .findByCustomerIdAndTransactionDateBetween(
                        customerId,
                        fromDate,
                        toDate
                );

        verifyNoInteractions(rewardCalculator);
    }

    @Test
    void shouldUseResolvedDateRange() {

        // Given
        String customerId = "CUST001";

        LocalDate requestedFromDate =
                LocalDate.of(2026, 6, 1);

        LocalDate requestedToDate =
                LocalDate.of(2026, 8, 31);

        LocalDate effectiveFromDate =
                LocalDate.of(2026, 5, 1);

        LocalDate effectiveToDate =
                LocalDate.of(2026, 8, 31);

        when(dateRangeResolver.resolve(
                requestedFromDate,
                requestedToDate))
                .thenReturn(
                        new DateRange(
                                effectiveFromDate,
                                effectiveToDate
                        )
                );

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));

        Transaction transaction =
                Transaction.builder()
                        .transactionId("TXN001")
                        .customerId(customerId)
                        .transactionDate(
                                LocalDate.of(2026, 6, 10))
                        .amount(
                                new BigDecimal("120.00"))
                        .build();

        when(transactionRepository
                .findByCustomerIdAndTransactionDateBetween(
                        customerId,
                        effectiveFromDate,
                        effectiveToDate))
                .thenReturn(List.of(transaction));

        when(rewardCalculator.calculate(
                new BigDecimal("120.00")))
                .thenReturn(90);

        // When
        RewardResponse response =
                rewardService.calculateRewards(
                        customerId,
                        requestedFromDate,
                        requestedToDate
                );

        // Then
        assertEquals(
                effectiveFromDate,
                response.getFromDate()
        );

        assertEquals(
                effectiveToDate,
                response.getToDate()
        );

        verify(transactionRepository)
                .findByCustomerIdAndTransactionDateBetween(
                        customerId,
                        effectiveFromDate,
                        effectiveToDate
                );
    }
}