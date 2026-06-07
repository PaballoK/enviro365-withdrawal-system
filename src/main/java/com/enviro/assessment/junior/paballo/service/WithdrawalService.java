package com.enviro.assessment.junior.paballo.service;

import com.enviro.assessment.junior.paballo.dto.WithdrawalRequestDTO;
import com.enviro.assessment.junior.paballo.dto.WithdrawalResponseDTO;

import java.util.List;

public interface WithdrawalService {

    WithdrawalResponseDTO withdraw(WithdrawalRequestDTO requestDTO);

    List<WithdrawalResponseDTO> getWithdrawalHistory(Long investorId);
}
