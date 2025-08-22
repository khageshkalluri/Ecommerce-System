package com.ecommerce.usersservice.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserPatchRequest {
    @Size(min = 3,max = 50)
    private String name;
    @Email
    private String email;
    @Size(min = 10,max = 50)
    private String address;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Past(message = "date of birth must be in the past")
    private LocalDate date_of_birth;
}
