package com.enviro.assessment.junior.paballo.service;

import com.enviro.assessment.junior.paballo.dto.DepositRequestDTO;
import com.enviro.assessment.junior.paballo.dto.TransactionResponseDTO;
import com.enviro.assessment.junior.paballo.dto.WithdrawalRequestDTO;
import com.enviro.assessment.junior.paballo.entity.Investor;
import com.enviro.assessment.junior.paballo.enums.TransactionType;
import com.enviro.assessment.junior.paballo.exception.AgeRestrictionException;
import com.enviro.assessment.junior.paballo.exception.InsufficientBalanceException;

import java.util.List;

/**
 * Service interface for processing and retrieving investor transactions.
 */
public interface TransactionService {

    /**
     * Processes a withdrawal request for an investor's product.
     * Validates business rules before deducting the amount from the product balance.
     *
     * @param requestDTO the withdrawal request containing the product ID and amount
     * @param investor the authenticated investor resolved from the JWT
     * @return a response DTO with the transaction details and balance after withdrawal
     * @throws AgeRestrictionException if a retirement product withdrawal is attempted below age 65
     * @throws InsufficientBalanceException if the amount exceeds 90% of the product balance
     */
    TransactionResponseDTO withdraw(WithdrawalRequestDTO requestDTO, Investor investor);

    /**
     * Processes a deposit request for an investor's product.
     *
     * @param request the deposit request containing the product ID and amount
     * @param investor the authenticated investor resolved from the JWT
     * @return a response DTO with the transaction details and balance after deposit
     */
    TransactionResponseDTO deposit(DepositRequestDTO request, Investor investor);

    /**
     * Returns all transactions for the given investor, ordered by most recent first.
     * Pass a {@link TransactionType} to filter by type, or {@code null} to return all.
     *
     * @param investor the authenticated investor resolved from the JWT
     * @param type optional filter — WITHDRAWAL, DEPOSIT, or null for all
     * @return a list of transaction response DTOs
     */
    List<TransactionResponseDTO> getTransactionHistory(Investor investor, TransactionType type);
}
