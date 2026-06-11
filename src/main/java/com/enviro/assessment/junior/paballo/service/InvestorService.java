package com.enviro.assessment.junior.paballo.service;

import com.enviro.assessment.junior.paballo.dto.InvestorPortfolioDTO;
import com.enviro.assessment.junior.paballo.entity.Investor;

/**
 * Service interface for investor-related operations.
 */
public interface InvestorService {

    /**
     * Retrieves the portfolio for the given investor, including all their products and total value.
     *
     * @param investor the authenticated investor resolved from the JWT
     * @return the investor's portfolio as a DTO
     */
    InvestorPortfolioDTO getPortfolio(Investor investor);
}
