package com.enviro.assessment.junior.paballo.dto;

import com.enviro.assessment.junior.paballo.enums.ProductType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {

    private Long id;
    private String productName;
    private ProductType productType;
    private BigDecimal balance;
}
