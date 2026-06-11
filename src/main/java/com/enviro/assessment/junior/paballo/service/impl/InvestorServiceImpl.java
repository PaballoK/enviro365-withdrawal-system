package com.enviro.assessment.junior.paballo.service.impl;

import com.enviro.assessment.junior.paballo.dto.InvestorPortfolioDTO;
import com.enviro.assessment.junior.paballo.dto.ProductDTO;
import com.enviro.assessment.junior.paballo.entity.Investor;
import com.enviro.assessment.junior.paballo.exception.InvestorNotFoundException;
import com.enviro.assessment.junior.paballo.repository.InvestorRepository;
import com.enviro.assessment.junior.paballo.service.InvestorService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Implementation of {@link InvestorService} that handles investor portfolio retrieval.
 */
@Service
@RequiredArgsConstructor
public class InvestorServiceImpl implements InvestorService {

    private static final Logger logger = LoggerFactory.getLogger(InvestorServiceImpl.class);

    private final InvestorRepository investorRepository;
    private final ModelMapper modelMapper;

    /**
     * Maps the authenticated investor's products to DTOs and calculates the total portfolio value.
     *
     * @param investor the authenticated investor resolved from the JWT
     * @return a fully populated {@link InvestorPortfolioDTO} with product details and total value
     */
    @Override
    public InvestorPortfolioDTO getPortfolio(Investor investor) {
        logger.debug("Fetching portfolio for investorId={}", investor.getId());

        Investor loaded = investorRepository.findByIdWithProducts(investor.getId())
                .orElseThrow(() -> new InvestorNotFoundException("Investor not found with id " + investor.getId()));

        List<ProductDTO> productDTOS = loaded.getProducts().stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        BigDecimal totalValue = productDTOS.stream()
                .map(ProductDTO::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        logger.debug("Portfolio loaded for investorId={}: {} products, totalValue={}",
                loaded.getId(), productDTOS.size(), totalValue);

        return InvestorPortfolioDTO.builder()
                .investorId(loaded.getId())
                .firstName(loaded.getFirstName())
                .lastName(loaded.getLastName())
                .email(loaded.getEmail())
                .products(productDTOS)
                .totalValue(totalValue)
                .build();
    }
}
