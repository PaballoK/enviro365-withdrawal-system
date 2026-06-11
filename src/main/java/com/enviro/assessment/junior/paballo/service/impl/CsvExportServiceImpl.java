package com.enviro.assessment.junior.paballo.service.impl;

import com.enviro.assessment.junior.paballo.dto.WithdrawalResponseDTO;
import com.enviro.assessment.junior.paballo.entity.Investor;
import com.enviro.assessment.junior.paballo.service.CsvExportService;
import com.enviro.assessment.junior.paballo.service.WithdrawalService;
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
public class CsvExportServiceImpl implements CsvExportService {

    private final WithdrawalService withdrawalService;

    @Override
    public String exportWithdrawalHistory(Investor investor, LocalDate startDate, LocalDate endDate) throws IOException {
        List<WithdrawalResponseDTO> history = withdrawalService.getWithdrawalHistory(investor);

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
