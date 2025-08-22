package com.ecommerce.usersservice.ExceptionHandlers;

public class UserDoesNotExist extends RuntimeException {
    public UserDoesNotExist(String message) {
        super(message);
    }
}
