package com.ecommerce.authservice.Services;

import com.ecommerce.authservice.Dto.AuthRequest;
import com.ecommerce.authservice.JWT.JwtService;
import com.ecommerce.authservice.Model.AuthUsers;
import com.ecommerce.authservice.Repository.AuthRepository;
import io.jsonwebtoken.JwtException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final AuthUserService authUserService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    public AuthService(AuthUserService authUserService,PasswordEncoder passwordEncoder,JwtService jwtService) {
        this.authUserService = authUserService;
        this.passwordEncoder = passwordEncoder ;
        this.jwtService = jwtService;
    }

    public Optional<String> authenticateUser(AuthRequest authRequest) {
        Optional<String> token = this.authUserService.getUser(authRequest.getEmail())
                .filter(u->passwordEncoder.matches(authRequest.getPassword(),u.getPassword()))
                .map(u -> jwtService.generateToken(u.getEmail(),u.getRole()));
        return token;
    }

    public boolean validateToken(String token) {
        try{
            this.jwtService.validateToken(token);
            return true;
        }
        catch (JwtException e){
            return false;
        }
    }

}
