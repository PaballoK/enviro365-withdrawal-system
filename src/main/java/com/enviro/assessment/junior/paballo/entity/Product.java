package com.enviro.assessment.junior.paballo.entity;

import com.enviro.assessment.junior.paballo.enums.ProductType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String productName;

    @Enumerated(EnumType.STRING)
    private ProductType productType;

    private BigDecimal balance;

    @ManyToOne
    @JoinColumn(name = "investor_id")
    private Investor investor;
}
