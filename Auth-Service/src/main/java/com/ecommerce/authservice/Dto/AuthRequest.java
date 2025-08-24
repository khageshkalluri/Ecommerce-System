package com.ecommerce.authservice.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthRequest {

    @NotNull(message = "email cannot be null")
    @NotBlank(message = "email cannot be blank")
    private String email;

    @NotNull(message = "password cannot be null")
    @NotBlank(message = "password cannot be blank")
    private String password;
}
