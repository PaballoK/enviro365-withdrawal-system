package com.enviro.assessment.junior.paballo.service;

import com.enviro.assessment.junior.paballo.entity.Investor;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String generateToken(Investor investor);
    String extractUsername(String token);
    boolean isValid(String token, UserDetails user);
}
