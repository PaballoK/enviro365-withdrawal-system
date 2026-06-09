package com.enviro.assessment.junior.paballo.service.impl;

import com.enviro.assessment.junior.paballo.dto.WithdrawalRequestDTO;
import com.enviro.assessment.junior.paballo.dto.WithdrawalResponseDTO;
import com.enviro.assessment.junior.paballo.entity.Investor;
import com.enviro.assessment.junior.paballo.entity.Product;
import com.enviro.assessment.junior.paballo.entity.WithdrawalNotice;
import com.enviro.assessment.junior.paballo.enums.ProductType;
import com.enviro.assessment.junior.paballo.exception.AgeRestrictionException;
import com.enviro.assessment.junior.paballo.exception.InsufficientBalanceException;
import com.enviro.assessment.junior.paballo.exception.InvestorNotFoundException;
import com.enviro.assessment.junior.paballo.finder.InvestorFinder;
import com.enviro.assessment.junior.paballo.finder.ProductFinder;
import com.enviro.assessment.junior.paballo.repository.InvestorRepository;
import com.enviro.assessment.junior.paballo.repository.ProductRepository;
import com.enviro.assessment.junior.paballo.repository.WithdrawalNoticeRepository;
import com.enviro.assessment.junior.paballo.service.WithdrawalService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

/**
 * Implementation of {@link WithdrawalService} that handles the business logic for
 * processing withdrawals and fetching withdrawal history.
 */
@Service
@RequiredArgsConstructor
public class WithdrawalServiceImpl implements WithdrawalService {

    private static final int RETIREMENT_AGE = 65;
    private static final BigDecimal MAX_WITHDRAWAL_PERCENT = new BigDecimal("0.90");


    private final InvestorRepository investorRepository;
    private final ProductRepository productRepository;
    private final WithdrawalNoticeRepository withdrawalNoticeRepository;

    private final ProductFinder productFinder;
    private final InvestorFinder investorFinder;

    /**
     * Processes a withdrawal by validating the investor and product, running business rule checks,
     * deducting the amount from the product balance, and saving a withdrawal notice record.
     *
     * @param request the withdrawal request containing investor ID, product ID, and amount
     * @return a WithdrawalResponseDTO with the saved withdrawal details
     * @throws InvestorNotFoundException if the investor does not exist
     * @throws AgeRestrictionException if a retirement product withdrawal is attempted and the investor is under 65
     * @throws InsufficientBalanceException if the requested amount is more than 90% of the product balance
     */
    @Transactional
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

    /**
     * Retrieves all withdrawal records for the given investor, ordered by most recent first.
     * Each record is mapped to a WithdrawalResponseDTO before being returned.
     *
     * @param investorId the ID of the investor
     * @return a list of withdrawal response DTOs, or an empty list if none exist
     */
    @Override
    public List<WithdrawalResponseDTO> getWithdrawalHistory(Long investorId) {

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

    /**
     * Checks that the investor meets the minimum retirement age of 65 before allowing
     * a withdrawal from a RETIREMENT product. Non-retirement products skip this check.
     *
     * @param investor the investor making the withdrawal
     * @param product  the product being withdrawn from
     * @throws AgeRestrictionException if the product is RETIREMENT and the investor is under 65
     */
    private void validateRetirementAge(Investor investor, Product product){

        if(product.getProductType() != ProductType.RETIREMENT){
            return;
        }

        int age = Period.between(investor.getBirthDate(), LocalDate.now()).getYears();

        if(age < RETIREMENT_AGE){
            throw new AgeRestrictionException("You cannot withdraw from your retirement unless you are 65 or older ");
        }
    }

    /**
     * Ensures the requested withdrawal amount does not exceed 90% of the product's current balance.
     * This rule prevents investors from fully emptying a product in a single withdrawal.
     *
     * @param product the product being withdrawn from
     * @param amount  the amount the investor wants to withdraw
     * @throws InsufficientBalanceException if the amount is greater than 90% of the balance
     */
    private void validateMaxWithdrawal(Product product, BigDecimal amount){

        BigDecimal maximumAllowed = product.getBalance()
                .multiply(MAX_WITHDRAWAL_PERCENT);

        if(amount.compareTo(maximumAllowed) > 0){
            throw new InsufficientBalanceException("Cannot withdraw more than 90 percent of your capital in this investment product");
        }

    }

}
