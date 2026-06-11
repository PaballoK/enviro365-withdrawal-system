package com.enviro.assessment.junior.paballo.dto;

import com.enviro.assessment.junior.paballo.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class TransactionResponseDTO {

    private Long id;
    private Long investorId;
    private Long productId;
    private String productName;
    private TransactionType type;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime processedAt;
}

