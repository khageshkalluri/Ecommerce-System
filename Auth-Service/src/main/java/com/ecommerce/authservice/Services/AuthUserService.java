package com.ecommerce.authservice.Services;

import com.ecommerce.authservice.Dto.AuthRequest;
import com.ecommerce.authservice.Model.AuthUsers;
import com.ecommerce.authservice.Repository.AuthRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthUserService {
    private AuthRepository authRepository;
    public AuthUserService(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public Optional<AuthUsers> getUser(String email) {
        return this.authRepository.findByEmail(email);
    }
}
