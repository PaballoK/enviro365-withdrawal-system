package com.enviro.assessment.junior.paballo.controller;

import com.enviro.assessment.junior.paballo.dto.WithdrawalRequestDTO;
import com.enviro.assessment.junior.paballo.dto.WithdrawalResponseDTO;
import com.enviro.assessment.junior.paballo.service.CsvExportService;
import com.enviro.assessment.junior.paballo.service.WithdrawalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Withdrawals", description = "Submit and retrieve investor withdrawal records")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/withdrawals")
public class WithdrawalController {

    private final WithdrawalService withdrawalService;
    private final CsvExportService csvExportService;

    @Operation(summary = "Submit a withdrawal", description = "Processes a withdrawal from an investor's product. " +
            "Enforces minimum balance and annual withdrawal limit rules.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Withdrawal processed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or business rule violation"),
            @ApiResponse(responseCode = "404", description = "Investor or product not found")
    })
    @PostMapping
    public ResponseEntity<WithdrawalResponseDTO> withdraw(@Valid @RequestBody WithdrawalRequestDTO request) {
        WithdrawalResponseDTO response = withdrawalService.withdraw(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get withdrawal history",
            description = "Returns all successful withdrawals for the given investor," +
            " ordered by date descending")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Withdrawal history retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Investor not found")
    })
    @GetMapping("/investor/{investorId}")
    public ResponseEntity<List<WithdrawalResponseDTO>> getWithdrawalHistory(@PathVariable Long investorId) {
        List<WithdrawalResponseDTO> history = withdrawalService.getWithdrawalHistory(investorId);
        return ResponseEntity.ok(history);
    }

    @Operation(summary = "Export withdrawal history as CSV",
            description = "Downloads a CSV statement of the investor's withdrawals." +
                    " Optionally filter by date range using startDate and endDate query parameters.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "CSV file generated and returned"),
            @ApiResponse(responseCode = "404", description = "Investor not found"),
            @ApiResponse(responseCode = "500", description = "Failed to generate CSV")
    })
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
