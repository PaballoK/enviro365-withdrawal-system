package com.enviro.assessment.junior.paballo.controller;

import com.enviro.assessment.junior.paballo.dto.InvestorPortfolioDTO;
import com.enviro.assessment.junior.paballo.service.InvestorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portfolio")
public class InvestorPortfolioController {

    private final InvestorService investorService;

    @GetMapping("/{id}")
    public ResponseEntity<InvestorPortfolioDTO> getPortfolio(@PathVariable Long id){

        return ResponseEntity.status(HttpStatus.OK).body(investorService.getPortfolio(id));
    }


}
