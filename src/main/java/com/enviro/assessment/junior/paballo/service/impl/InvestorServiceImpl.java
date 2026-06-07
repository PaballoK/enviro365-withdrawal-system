package com.enviro.assessment.junior.paballo.service.impl;

import com.enviro.assessment.junior.paballo.dto.InvestorPortfolioDTO;
import com.enviro.assessment.junior.paballo.dto.ProductDTO;
import com.enviro.assessment.junior.paballo.entity.Investor;
import com.enviro.assessment.junior.paballo.entity.Product;
import com.enviro.assessment.junior.paballo.exception.InvestorNotFoundException;
import com.enviro.assessment.junior.paballo.repository.InvestorRepository;
import com.enviro.assessment.junior.paballo.service.InvestorService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvestorServiceImpl implements InvestorService {

    private final InvestorRepository investorRepository;
    private final ModelMapper modelMapper;

    @Override
    public InvestorPortfolioDTO getPortfolio(Long investorId) {

        Investor investor = investorRepository.findById(investorId)
                .orElseThrow(() -> new InvestorNotFoundException("Investor Not found"));

        List<ProductDTO> productDTOS = investor.getProducts().stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        BigDecimal totalValue = productDTOS.stream()
                .map(ProductDTO::getBalance
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return InvestorPortfolioDTO.builder()
                .investorId(investor.getId())
                .firstName(investor.getFirstName())
                .lastName(investor.getLastName())
                .email(investor.getEmail())
                .products(productDTOS)
                .totalValue(totalValue)
                .build();
    }
}
