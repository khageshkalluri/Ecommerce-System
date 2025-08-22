package com.ecommerce.usersservice.Mappers;

import com.ecommerce.usersservice.Dto.UserRequest;
import com.ecommerce.usersservice.Dto.UsersDto;
import com.ecommerce.usersservice.model.Users;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;


public class UserMapper {
    public static UsersDto mapToDto(Users user) {
     return UsersDto.builder().id(user.getId()).email(user.getEmail()).address(user.getAddress()).name(user.getName()).build();
    }
    public static Users mapToEntity(UserRequest userRequest) {
        return Users.builder().name(userRequest.getName()).email(userRequest.getEmail()).address(userRequest.getAddress()).
                date_of_birth(userRequest.getDate_of_birth()).registered_date(LocalDate.now()).build();
    }

}
