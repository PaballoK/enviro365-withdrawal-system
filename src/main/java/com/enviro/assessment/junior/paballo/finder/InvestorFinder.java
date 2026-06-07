package com.enviro.assessment.junior.paballo.finder;

import com.enviro.assessment.junior.paballo.entity.Investor;
import com.enviro.assessment.junior.paballo.exception.InvestorNotFoundException;
import com.enviro.assessment.junior.paballo.repository.InvestorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvestorFinder {

    private final InvestorRepository investorRepository;

    public Investor getInvestorByIdOrThrow(Long investorId){
        return investorRepository.findById(investorId)
                .orElseThrow(()-> new InvestorNotFoundException(" Investor not found with id " + investorId));
    }
}
