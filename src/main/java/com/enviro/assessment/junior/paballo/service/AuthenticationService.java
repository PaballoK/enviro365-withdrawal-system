package com.enviro.assessment.junior.paballo.service;

import com.enviro.assessment.junior.paballo.dto.AuthenticationResponseDTO;
import com.enviro.assessment.junior.paballo.dto.LoginRequestDTO;

public interface AuthenticationService {

    AuthenticationResponseDTO login(LoginRequestDTO request);
}
