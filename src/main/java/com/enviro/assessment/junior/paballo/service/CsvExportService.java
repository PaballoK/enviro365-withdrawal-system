package com.enviro.assessment.junior.paballo.service;

import com.enviro.assessment.junior.paballo.entity.Investor;

import java.io.IOException;
import java.time.LocalDate;

public interface CsvExportService {
    String exportWithdrawalHistory(Investor investor, LocalDate startDate, LocalDate endDate) throws IOException;
}
