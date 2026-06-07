package com.enviro.assessment.junior.paballo.service;

import com.enviro.assessment.junior.paballo.dto.WithdrawalResponseDTO;
import com.enviro.assessment.junior.paballo.finder.InvestorFinder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CsvExportService {

    private final WithdrawalService withdrawalService;
    private final InvestorFinder investorFinder;

    public String exportWithdrawalHistory(Long investorId, LocalDate startDate, LocalDate endDate) throws IOException {

        investorFinder.getInvestorByIdOrThrow(investorId);

        List<WithdrawalResponseDTO> history = withdrawalService.getWithdrawalHistory(investorId);

        if (startDate != null) {
            history = history.stream()
                    .filter(w -> !w.getProcessedAt().toLocalDate().isBefore(startDate))
                    .toList();
        }
        if (endDate != null) {
            history = history.stream()
                    .filter(w -> !w.getProcessedAt().toLocalDate().isAfter(endDate))
                    .toList();
        }

        StringWriter writer = new StringWriter();
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader("ID", "Investor ID", "Product ID", "Product Name", "Amount", "Remaining Balance", "Processed At")
                .build();

        try (CSVPrinter printer = new CSVPrinter(writer, format)) {
            for (WithdrawalResponseDTO w : history) {
                printer.printRecord(
                        w.getId(),
                        w.getInvestorId(),
                        w.getProductId(),
                        w.getProductName(),
                        w.getAmount(),
                        w.getRemainingBalance(),
                        w.getProcessedAt()
                );
            }
        }

        return writer.toString();
    }
}