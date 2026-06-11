package com.enviro.assessment.junior.paballo.service.impl;

import com.enviro.assessment.junior.paballo.dto.TransactionResponseDTO;
import com.enviro.assessment.junior.paballo.entity.Investor;
import com.enviro.assessment.junior.paballo.service.CsvExportService;
import com.enviro.assessment.junior.paballo.service.TransactionService;
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

    private final TransactionService transactionService;

    @Override
    public String exportTransactionHistory(Investor investor, LocalDate startDate, LocalDate endDate) throws IOException {
        List<TransactionResponseDTO> history = transactionService.getTransactionHistory(investor, null);

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
                .setHeader("ID", "Investor ID", "Product ID", "Product Name", "Type", "Amount", "Balance After", "Processed At")
                .build();

        try (CSVPrinter printer = new CSVPrinter(writer, format)) {
            for (TransactionResponseDTO w : history) {
                printer.printRecord(
                        w.getId(),
                        w.getInvestorId(),
                        w.getProductId(),
                        w.getProductName(),
                        w.getType(),
                        w.getAmount(),
                        w.getBalanceAfter(),
                        w.getProcessedAt()
                );
            }
        }

        return writer.toString();
    }
}
