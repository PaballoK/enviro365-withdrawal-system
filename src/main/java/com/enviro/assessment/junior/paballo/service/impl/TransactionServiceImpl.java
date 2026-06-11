package com.enviro.assessment.junior.paballo.service.impl;

import com.enviro.assessment.junior.paballo.dto.DepositRequestDTO;
import com.enviro.assessment.junior.paballo.dto.TransactionResponseDTO;
import com.enviro.assessment.junior.paballo.dto.WithdrawalRequestDTO;
import com.enviro.assessment.junior.paballo.entity.Investor;
import com.enviro.assessment.junior.paballo.entity.Product;
import com.enviro.assessment.junior.paballo.entity.Transaction;
import com.enviro.assessment.junior.paballo.enums.ProductType;
import com.enviro.assessment.junior.paballo.enums.TransactionType;
import com.enviro.assessment.junior.paballo.exception.AgeRestrictionException;
import com.enviro.assessment.junior.paballo.exception.InsufficientBalanceException;
import com.enviro.assessment.junior.paballo.exception.ProductNotFoundException;
import com.enviro.assessment.junior.paballo.repository.ProductRepository;
import com.enviro.assessment.junior.paballo.repository.TransactionRepository;
import com.enviro.assessment.junior.paballo.service.TransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

/**
 * Implementation of {@link TransactionService} that handles the business logic for
 * processing deposits and withdrawals, and fetching transaction history.
 */
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private static final int RETIREMENT_AGE = 65;
    private static final BigDecimal MAX_WITHDRAWAL_PERCENT = new BigDecimal("0.90");


    private final ProductRepository productRepository;
    private final TransactionRepository transactionRepository;

    /**
     * Processes a withdrawal by validating the investor and product, running business rule checks,
     * deducting the amount from the product balance, and saving a withdrawal notice record.
     *
     * @param request the withdrawal request containing investor ID, product ID, and amount
     * @return a WithdrawalResponseDTO with the saved withdrawal details
     * @throws AgeRestrictionException if a retirement product withdrawal is attempted and the investor is under 65
     * @throws InsufficientBalanceException if the requested amount is more than 90% of the product balance
     */
    @Transactional
    @Override
    public TransactionResponseDTO withdraw(WithdrawalRequestDTO request, Investor investor) {
        logger.info("Processing withdrawal of {} for investorId={} on productId={}",
                request.getWithdrawalAmount(), investor.getId(), request.getProductId());

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id " + request.getProductId()));

        validateRetirementAge(investor,product);
        validateMaxWithdrawal(product,request.getWithdrawalAmount());

        BigDecimal remainingBalance = product.getBalance().subtract(request.getWithdrawalAmount());

        product.setBalance(remainingBalance);
        productRepository.save(product);

        Transaction withdrawalNotice = Transaction.builder()
                .amount(request.getWithdrawalAmount())
                .product(product)
                .investor(investor)
                .balance(remainingBalance)
                .transactionType(TransactionType.WITHDRAW)
                .processedAt(LocalDateTime.now())
                .build();

        Transaction saved = transactionRepository.save(withdrawalNotice);

        logger.info("Withdrawal {} completed: investorId={}, productId={}, balanceAfter={}",
                saved.getId(), investor.getId(), product.getId(), remainingBalance);

        return TransactionResponseDTO.builder()
                .amount(saved.getAmount())
                .productName(product.getProductName())
                .productId(product.getId())
                .investorId(investor.getId())
                .id(saved.getId())
                .processedAt(saved.getProcessedAt())
                .balanceAfter(saved.getBalance())
                .type(TransactionType.WITHDRAW)
                .build();
    }

    @Transactional
    @Override
    public TransactionResponseDTO deposit(DepositRequestDTO request, Investor investor) {
        logger.info("Processing deposit of {} for investorId={} on productId={}",
                request.getDepositAmount(), investor.getId(), request.getProductId());

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(()-> new ProductNotFoundException("Product not found with id " + request.getProductId()));

        BigDecimal newBalance = product.getBalance().add(request.getDepositAmount());

        product.setBalance(newBalance);
        productRepository.save(product);

        Transaction depositNotice = Transaction.builder()
                .product(product)
                .transactionType(TransactionType.DEPOSIT)
                .amount(request.getDepositAmount())
                .balance(product.getBalance())
                .processedAt(LocalDateTime.now())
                .investor(investor)
                .build();

        Transaction saved = transactionRepository.save(depositNotice);

        logger.info("Deposit {} completed: investorId={}, productId={}, balanceAfter={}",
                saved.getId(), investor.getId(), product.getId(), newBalance);

        return TransactionResponseDTO.builder()
                .amount(saved.getAmount())
                .productName(product.getProductName())
                .productId(product.getId())
                .investorId(investor.getId())
                .id(saved.getId())
                .type(TransactionType.DEPOSIT)
                .processedAt(saved.getProcessedAt())
                .balanceAfter(saved.getBalance())
                .build();
    }

    @Override
    public List<TransactionResponseDTO> getTransactionHistory(Investor investor, TransactionType type) {

        List<Transaction> transactions = type == null
                ? transactionRepository.findByInvestorIdOrderByProcessedAtDesc(investor.getId())
                : transactionRepository.findByInvestorIdAndTransactionTypeOrderByProcessedAtDesc(investor.getId(), type);

        return transactions.stream()
                .map(t -> TransactionResponseDTO.builder()
                        .id(t.getId())
                        .productId(t.getProduct().getId())
                        .productName(t.getProduct().getProductName())
                        .processedAt(t.getProcessedAt())
                        .investorId(t.getInvestor().getId())
                        .amount(t.getAmount())
                        .balanceAfter(t.getBalance())
                        .type(t.getTransactionType())
                        .build())
                .toList();
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
