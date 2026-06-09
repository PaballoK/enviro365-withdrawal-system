package com.enviro.assessment.junior.paballo.service;

import com.enviro.assessment.junior.paballo.dto.WithdrawalRequestDTO;
import com.enviro.assessment.junior.paballo.dto.WithdrawalResponseDTO;
import com.enviro.assessment.junior.paballo.exception.AgeRestrictionException;
import com.enviro.assessment.junior.paballo.exception.InsufficientBalanceException;
import com.enviro.assessment.junior.paballo.exception.InvestorNotFoundException;

import java.util.List;

/**
 * Service interface for processing and retrieving investor withdrawals.
 */
public interface WithdrawalService {

    /**
     * Processes a withdrawal request for an investor's product.
     * Validates business rules before deducting the amount from the product balance.
     *
     * @param requestDTO the withdrawal request containing the investor ID, product ID, and amount
     * @return a response DTO with the withdrawal details and remaining balance
     * @throws InvestorNotFoundException if the investor does not exist
     * @throws AgeRestrictionException if a retirement product withdrawal is attempted below age 65
     * @throws InsufficientBalanceException if the amount exceeds 90% of the product balance
     */
    WithdrawalResponseDTO withdraw(WithdrawalRequestDTO requestDTO);

    /**
     * Returns all past withdrawals for the given investor, ordered by most recent first.
     *
     * @param investorId the ID of the investor
     * @return a list of withdrawal response DTOs
     */
    List<WithdrawalResponseDTO> getWithdrawalHistory(Long investorId);
}
