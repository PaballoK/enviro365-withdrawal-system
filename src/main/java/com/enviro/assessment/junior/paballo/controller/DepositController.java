package com.enviro.assessment.junior.paballo.controller;

import com.enviro.assessment.junior.paballo.annotations.CurrentUser;
import com.enviro.assessment.junior.paballo.dto.DepositRequestDTO;
import com.enviro.assessment.junior.paballo.dto.TransactionResponseDTO;
import com.enviro.assessment.junior.paballo.entity.Investor;
import com.enviro.assessment.junior.paballo.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Deposits", description = "Submit deposits into investor product accounts")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/deposits")
public class DepositController {

    private final TransactionService transactionService;

    @Operation(summary = "Submit a deposit", description = "Adds funds to an investor's product balance.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Deposit processed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PostMapping
    public ResponseEntity<TransactionResponseDTO> deposit(@Valid @RequestBody DepositRequestDTO request,
                                                          @CurrentUser Investor investor) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.deposit(request, investor));
    }
}
