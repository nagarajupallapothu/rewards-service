package com.customer.rewards.controller;

import com.customer.rewards.dto.response.RewardResponse;
import com.customer.rewards.service.RewardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(
        name = "Customer Rewards",
        description = "APIs for calculating customer reward points"
)
public class RewardController {

    private final RewardService rewardService;

    @Operation(
            summary = "Calculate customer rewards",
            description = """
                    Calculates reward points earned by a customer
                    for all transactions within the specified date range.
                    
                    The response contains transaction-level reward points,
                    monthly reward summaries, and the overall reward total.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Reward calculation completed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RewardResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid customer ID or date range"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Customer not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @GetMapping("/{customerId}/rewards")
    public ResponseEntity<RewardResponse> getRewards(
            @Parameter(
                    description = "Unique customer identifier",
                    example = "CUST001",
                    required = true
            )
            @NotBlank(message = "customerId must not be blank")
            @PathVariable String customerId,

            @Parameter(
                    description = "Optional start date. "
                            + "If omitted, the default date range is applied.",
                    example = "2026-06-01"
            )
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @Parameter(
                    description = "Optional end date. "
                            + "If omitted, the default date range is applied.",
                    example = "2026-08-31"
            )
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate) {

        log.info(
                "Received reward request. customerId={}, fromDate={}, toDate={}",
                customerId,
                fromDate,
                toDate
        );
        RewardResponse response =
                rewardService.calculateRewards(
                        customerId,
                        fromDate,
                        toDate
                );

        log.info(
                "Reward request completed successfully. customerId={}",
                customerId
        );
        return ResponseEntity.ok(response);
    }
}