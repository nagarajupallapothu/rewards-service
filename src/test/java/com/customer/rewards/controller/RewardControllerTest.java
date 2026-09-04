package com.customer.rewards.controller;

import com.customer.rewards.dto.response.RewardResponse;
import com.customer.rewards.exception.CustomerNotFoundException;
import com.customer.rewards.service.RewardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RewardController.class)
class RewardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RewardService rewardService;


    @Test
    void shouldReturnRewardsWhenValidRequestIsProvided()
            throws Exception {

        // Given
        String customerId = "CUST001";

        LocalDate fromDate =
                LocalDate.of(2026, 6, 1);

        LocalDate toDate =
                LocalDate.of(2026, 8, 31);

        RewardResponse response =
                RewardResponse.builder()
                        .fromDate(fromDate)
                        .toDate(toDate)
                        .build();

        when(rewardService.calculateRewards(
                eq(customerId),
                eq(fromDate),
                eq(toDate)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(
                        get("/api/v1/customers/{customerId}/rewards",
                                customerId)
                                .param("fromDate", "2026-06-01")
                                .param("toDate", "2026-08-31")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.fromDate")
                                .value("2026-06-01")
                )
                .andExpect(
                        jsonPath("$.toDate")
                                .value("2026-08-31")
                );

        verify(rewardService)
                .calculateRewards(
                        customerId,
                        fromDate,
                        toDate
                );
    }


    @Test
    void shouldCalculateRewardsWhenDatesAreNotProvided()
            throws Exception {

        // Given
        String customerId = "CUST001";

        RewardResponse response =
                RewardResponse.builder()
                        .fromDate(
                                LocalDate.of(2026, 6, 1))
                        .toDate(
                                LocalDate.of(2026, 9, 1))
                        .build();

        when(rewardService.calculateRewards(
                eq(customerId),
                eq(null),
                eq(null)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(
                        get("/api/v1/customers/{customerId}/rewards",
                                customerId)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.fromDate")
                                .value("2026-06-01")
                )
                .andExpect(
                        jsonPath("$.toDate")
                                .value("2026-09-01")
                );

        verify(rewardService)
                .calculateRewards(
                        customerId,
                        null,
                        null
                );
    }


    @Test
    void shouldAcceptOnlyFromDate()
            throws Exception {

        // Given
        String customerId = "CUST001";

        LocalDate fromDate =
                LocalDate.of(2026, 7, 1);

        RewardResponse response =
                RewardResponse.builder()
                        .fromDate(fromDate)
                        .toDate(LocalDate.of(2026, 9, 1))
                        .build();

        when(rewardService.calculateRewards(
                eq(customerId),
                eq(fromDate),
                eq(null)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(
                        get("/api/v1/customers/{customerId}/rewards",
                                customerId)
                                .param("fromDate", "2026-07-01")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        verify(rewardService)
                .calculateRewards(
                        customerId,
                        fromDate,
                        null
                );
    }


    @Test
    void shouldAcceptOnlyToDate()
            throws Exception {

        // Given
        String customerId = "CUST001";

        LocalDate toDate =
                LocalDate.of(2026, 8, 31);

        RewardResponse response =
                RewardResponse.builder()
                        .fromDate(
                                LocalDate.of(2026, 5, 31))
                        .toDate(toDate)
                        .build();

        when(rewardService.calculateRewards(
                eq(customerId),
                eq(null),
                eq(toDate)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(
                        get("/api/v1/customers/{customerId}/rewards",
                                customerId)
                                .param("toDate", "2026-08-31")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        verify(rewardService)
                .calculateRewards(
                        customerId,
                        null,
                        toDate
                );
    }


    @Test
    void shouldReturn404WhenCustomerDoesNotExist()
            throws Exception {

        // Given
        String customerId = "INVALID";

        when(rewardService.calculateRewards(
                eq(customerId),
                eq(null),
                eq(null)))
                .thenThrow(
                        new CustomerNotFoundException(
                                customerId
                        )
                );

        // When & Then
        mockMvc.perform(
                        get("/api/v1/customers/{customerId}/rewards",
                                customerId)
                )
                .andExpect(status().isNotFound());
    }


    @Test
    void shouldReturn400WhenFromDateHasInvalidFormat()
            throws Exception {

        // When & Then
        mockMvc.perform(
                        get("/api/v1/customers/CUST001/rewards")
                                .param("fromDate", "invalid-date")
                                .param("toDate", "2026-08-31")
                )
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldReturn400WhenToDateHasInvalidFormat()
            throws Exception {

        // When & Then
        mockMvc.perform(
                        get("/api/v1/customers/CUST001/rewards")
                                .param("fromDate", "2026-06-01")
                                .param("toDate", "invalid-date")
                )
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldReturn404WhenCustomerIdIsMissing()
            throws Exception {

        // When & Then
        mockMvc.perform(
                        get("/api/v1/customers//rewards")
                )
                .andExpect(status().isNotFound());
    }


    @Test
    void shouldReturn404WhenEndpointPathIsIncorrect()
            throws Exception {

        // When & Then
        mockMvc.perform(
                        get("/api/v1/customer/CUST001/rewards")
                )
                .andExpect(status().isNotFound());
    }
}
