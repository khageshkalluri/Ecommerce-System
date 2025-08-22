package com.ecommerce.usersservice.Service;

import com.ecommerce.usersservice.Dto.UserPatchRequest;
import com.ecommerce.usersservice.Dto.UserRequest;
import com.ecommerce.usersservice.Dto.UsersDto;

import java.util.List;

public interface UserService {
    List<UsersDto> getAllUsers();
    UsersDto addUser(UserRequest userRequest);
    UsersDto updateUser(UserRequest userRequest);
    UsersDto patchUsers(UserPatchRequest userRequest);
    UsersDto getUserByEmail(String email);
    List<UsersDto> getUserByUsername(String username);
    void deleteUserByEmail(String email);
}
