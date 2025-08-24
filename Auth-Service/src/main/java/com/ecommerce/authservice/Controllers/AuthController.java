package com.ecommerce.authservice.Controllers;


import com.ecommerce.authservice.Dto.AuthRequest;
import com.ecommerce.authservice.Dto.AuthResponse;
import com.ecommerce.authservice.Services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@Tag(name = "AuthService",description = "Operations of Auth Service")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(description = "Users login functionality",requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "login request body",
            content = @Content(
                    schema = @Schema(implementation = AuthRequest.class)
            )
    ),
    responses = {
            @ApiResponse(
                    description = "Auth response sample",
                    responseCode = "200",
                    content = @Content(
                            schema=@Schema(implementation = AuthResponse.class)
                    )
            )
    })
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        Optional<String> token=this.authService.authenticateUser(request);
        if(token.isPresent()){
            return new ResponseEntity<>(new AuthResponse(token.get()), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/validate")
    @Operation(description = "validate the jwt token response",requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = false
    ))
    public ResponseEntity<Void> validate(@RequestHeader("Authorization") String token) {
        if(token.isBlank() || !token.contains("Bearer ")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
       Boolean isValid= this.authService.validateToken(token.split(" ")[1]);
        if(isValid) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
