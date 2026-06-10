package com.enviro.assessment.junior.paballo.service;

import com.enviro.assessment.junior.paballo.dto.WithdrawalResponseDTO;
import com.enviro.assessment.junior.paballo.entity.Investor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.List;

/**
 * Service responsible for generating CSV exports of an investor's withdrawal history.
 */
@Service
@RequiredArgsConstructor
public class CsvExportService {

    private final WithdrawalService withdrawalService;

    /**
     * Builds a CSV string of all withdrawals for the given investor.
     * If startDate or endDate are provided, only withdrawals within that range are included.
     *
     * @param investorId the ID of the investor
     * @param startDate  optional start of the date range, can be null
     * @param endDate    optional end of the date range, can be null
     * @return a CSV-formatted string with headers and one row per withdrawal
     * @throws IOException if writing to the CSV buffer fails
     * @throws InvestorNotFoundException if no investor exists with the given ID
     */
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