package com.ecommerce.usersservice.Dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import org.hibernate.sql.Update;

import java.time.LocalDate;

@Data
@Builder
public class UserRequest {
    @NotBlank(message = "name field cannot be blank")
    @NotNull(message = "name field cannot be null")
    @NotEmpty(message = "name field cannot be empty")
    private String name;
    @Email
    @NotNull(message = "email field cannot be null")
    @NotEmpty(message = "email field cannot be empty")
    private String email;
    @NotBlank(message = "address field cannot be blank")
    @NotNull(message = "address field cannot be null")
    @NotEmpty(message = "address field cannot be empty")
    private String address;
    @NotNull(message = "Date of birth cannot be null or empty")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Past(message = "date of birth must be in the past")
    private LocalDate date_of_birth;
    @NotNull(groups = UpdateUserValidation.class,message = "registered_date is required")
    private LocalDate registered_date;
}
