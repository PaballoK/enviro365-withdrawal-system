package com.enviro.assessment.junior.paballo.controller;

import com.enviro.assessment.junior.paballo.dto.WithdrawalRequestDTO;
import com.enviro.assessment.junior.paballo.dto.WithdrawalResponseDTO;
import com.enviro.assessment.junior.paballo.service.WithdrawalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/withdrawals")
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    @PostMapping
    public ResponseEntity<WithdrawalResponseDTO> withdraw(@Valid @RequestBody WithdrawalRequestDTO request) {
        WithdrawalResponseDTO response = withdrawalService.withdraw(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/investor/{investorId}")
    public ResponseEntity<List<WithdrawalResponseDTO>> getWithdrawalHistory(@PathVariable Long investorId) {
        List<WithdrawalResponseDTO> history = withdrawalService.getWithdrawalHistory(investorId);
        return ResponseEntity.ok(history);
    }
}
