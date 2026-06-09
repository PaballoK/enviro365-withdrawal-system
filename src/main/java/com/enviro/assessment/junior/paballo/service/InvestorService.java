package com.enviro.assessment.junior.paballo.service;

import com.enviro.assessment.junior.paballo.dto.InvestorPortfolioDTO;

/**
 * Service interface for investor-related operations.
 */
public interface InvestorService {

    /**
     * Retrieves the portfolio for the given investor, including all their products and total value.
     *
     * @param investorId the ID of the investor
     * @return the investor's portfolio as a DTO
     * @throws com.enviro.assessment.junior.paballo.exception.InvestorNotFoundException if no investor exists with the given ID
     */
    InvestorPortfolioDTO getPortfolio(Long investorId);
}
