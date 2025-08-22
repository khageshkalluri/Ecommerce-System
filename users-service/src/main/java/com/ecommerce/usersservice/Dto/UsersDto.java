package com.ecommerce.usersservice.Dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class UsersDto {
    public UUID id;
    public String name;
    public String email;
    public String address;
}
