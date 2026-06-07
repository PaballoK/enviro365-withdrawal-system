package com.enviro.assessment.junior.paballo.service;

import com.enviro.assessment.junior.paballo.dto.InvestorPortfolioDTO;

public interface InvestorService {

    InvestorPortfolioDTO getPortfolio(Long investorId);
}
