package com.enviro.assessment.junior.paballo.controller;

import com.enviro.assessment.junior.paballo.dto.InvestorPortfolioDTO;
import com.enviro.assessment.junior.paballo.service.InvestorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Investor Portfolio", description = "Retrieve investor portfolio details")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portfolio")
public class InvestorPortfolioController {

    private final InvestorService investorService;

    @Operation(summary = "Get investor portfolio", description = "Returns the portfolio of a the given investor, " +
            "including all related products and their current balances")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Portfolio retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Investor not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<InvestorPortfolioDTO> getPortfolio(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(investorService.getPortfolio(id));
    }
}
