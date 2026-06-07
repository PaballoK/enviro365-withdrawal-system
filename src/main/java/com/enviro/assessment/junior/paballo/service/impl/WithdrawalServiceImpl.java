package com.enviro.assessment.junior.paballo.service.impl;

import com.enviro.assessment.junior.paballo.dto.WithdrawalRequestDTO;
import com.enviro.assessment.junior.paballo.dto.WithdrawalResponseDTO;
import com.enviro.assessment.junior.paballo.entity.Investor;
import com.enviro.assessment.junior.paballo.entity.Product;
import com.enviro.assessment.junior.paballo.entity.WithdrawalNotice;
import com.enviro.assessment.junior.paballo.enums.ProductType;
import com.enviro.assessment.junior.paballo.exception.AgeRestrictionException;
import com.enviro.assessment.junior.paballo.exception.InsufficientBalanceException;
import com.enviro.assessment.junior.paballo.finder.InvestorFinder;
import com.enviro.assessment.junior.paballo.finder.ProductFinder;
import com.enviro.assessment.junior.paballo.repository.InvestorRepository;
import com.enviro.assessment.junior.paballo.repository.ProductRepository;
import com.enviro.assessment.junior.paballo.repository.WithdrawalNoticeRepository;
import com.enviro.assessment.junior.paballo.service.WithdrawalService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WithdrawalServiceImpl implements WithdrawalService {


    private static final int RETIREMENT_AGE = 65;
    private static final BigDecimal MAX_WITHDRAWAL_PERCENT = new BigDecimal("0.90");


    private final InvestorRepository investorRepository;
    private final ProductRepository productRepository;
    private final WithdrawalNoticeRepository withdrawalNoticeRepository;
    private final ModelMapper modelMapper;

    private final ProductFinder productFinder;
    private final InvestorFinder investorFinder;

    @Override
    public WithdrawalResponseDTO withdraw(WithdrawalRequestDTO request) {

        Investor investor = investorFinder.getInvestorByIdOrThrow(request.getInvestorId());

        Product product = productFinder.getProductByIdOrThrow(request.getProductId());

        validateRetirementAge(investor,product);
        validateMaxWithdrawal(product,request.getWithdrawalAmount());

        BigDecimal remainingBalance = product.getBalance().subtract(request.getWithdrawalAmount());

        product.setBalance(remainingBalance);
        productRepository.save(product);

        WithdrawalNotice withdrawalNotice = WithdrawalNotice.builder()
                .amount(request.getWithdrawalAmount())
                .product(product)
                .investor(investor)
                .balance(remainingBalance)
                .processedAt(LocalDateTime.now())
                .build();

        WithdrawalNotice saved = withdrawalNoticeRepository.save(withdrawalNotice);

        return WithdrawalResponseDTO.builder()
                .amount(saved.getAmount())
                .productName(product.getProductName())
                .productId(product.getId())
                .investorId(investor.getId())
                .id(saved.getId())
                .processedAt(saved.getProcessedAt())
                .remainingBalance(saved.getBalance())
                .build();
    }

    @Override
    public List<WithdrawalResponseDTO> getWithdrawalHistory(Long investorId) {

        Investor investor = investorFinder.getInvestorByIdOrThrow(investorId);

        return withdrawalNoticeRepository.findByInvestorIdOrderByProcessedAtDesc(investorId).stream()
                .map(withdrawalNotice -> WithdrawalResponseDTO.builder()
                        .id(withdrawalNotice.getId())
                        .productId(withdrawalNotice.getProduct().getId())
                        .productName(withdrawalNotice.getProduct().getProductName())
                        .processedAt(withdrawalNotice.getProcessedAt())
                        .investorId(withdrawalNotice.getInvestor().getId())
                        .amount(withdrawalNotice.getAmount())
                        .remainingBalance(withdrawalNotice.getBalance())
                        .build()).toList();
    }

    private void validateRetirementAge(Investor investor, Product product){

        if(product.getProductType() != ProductType.RETIREMENT){
            return;
        }

        int age = Period.between(investor.getBirthDate(), LocalDate.now()).getYears();

        if(age <= RETIREMENT_AGE){
            throw new AgeRestrictionException("You cannot withdraw from your retirement unless you are 65 or older ");
        }
    }

    private void validateMaxWithdrawal(Product product, BigDecimal amount){

        BigDecimal maximumAllowed = product.getBalance()
                .multiply(MAX_WITHDRAWAL_PERCENT);

        if(amount.compareTo(maximumAllowed) > 0){
            throw new InsufficientBalanceException("Cannot withdraw more than 90 percent of your capital in this investment product");
        }

    }

}
