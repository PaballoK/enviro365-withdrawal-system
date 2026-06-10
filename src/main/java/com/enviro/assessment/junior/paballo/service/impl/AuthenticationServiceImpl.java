package com.enviro.assessment.junior.paballo.service.impl;

import com.enviro.assessment.junior.paballo.dto.AuthenticationResponseDTO;
import com.enviro.assessment.junior.paballo.dto.LoginRequestDTO;
import com.enviro.assessment.junior.paballo.entity.Investor;

import com.enviro.assessment.junior.paballo.repository.InvestorRepository;
import com.enviro.assessment.junior.paballo.service.AuthenticationService;
import com.enviro.assessment.junior.paballo.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final InvestorRepository investorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    @Override
    public AuthenticationResponseDTO login(LoginRequestDTO request) {

        String email = request.getEmail().toLowerCase().trim();

        Investor investor = investorRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));
        

        if (!passwordEncoder.matches(request.getPassword(), investor.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        String token = jwtService.generateToken(investor);

        return AuthenticationResponseDTO.builder()
                .token(token)
                .build();
    }
}
