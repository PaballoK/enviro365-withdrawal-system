package com.enviro.assessment.junior.paballo.controller;

import com.enviro.assessment.junior.paballo.dto.WithdrawalRequestDTO;
import com.enviro.assessment.junior.paballo.dto.WithdrawalResponseDTO;
import com.enviro.assessment.junior.paballo.service.CsvExportService;
import com.enviro.assessment.junior.paballo.service.WithdrawalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/withdrawals")
public class WithdrawalController {

    private final WithdrawalService withdrawalService;
    private final CsvExportService csvExportService;

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

    @GetMapping("/investor/{investorId}/export")
    public ResponseEntity<byte[]> exportWithdrawalHistory(
            @PathVariable Long investorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            byte[] csvBytes = csvExportService.exportWithdrawalHistory(investorId, startDate, endDate).getBytes();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"investor_" + investorId + "_statement.csv\"")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(csvBytes);

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate CSV", e);
        }
    }
}
