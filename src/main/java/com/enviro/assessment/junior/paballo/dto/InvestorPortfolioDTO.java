package com.enviro.assessment.junior.paballo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvestorPortfolioDTO {

    private Long investorId;
    private String firstName;
    private String lastName;
    private String email;
    private List<ProductDTO> products;
    private BigDecimal totalValue;
}
