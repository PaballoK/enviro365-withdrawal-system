package com.enviro.assessment.junior.paballo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WithdrawalResponseDTO {

    private Long id;
    private Long investorId;
    private Long productId;
    private String productName;
    private BigDecimal amount;
    private BigDecimal remainingBalance;
    private LocalDateTime processedAt;
}
