package com.ecommerce.usersservice.ExceptionHandlers;

import com.ecommerce.usersservice.Dto.UserRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends RuntimeException {

   @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
       HashMap<String, String> errors = new HashMap<>();
       ex.getBindingResult().getFieldErrors().forEach((fieldError) -> {
           errors.put(fieldError.getField(), fieldError.getDefaultMessage());
       });
       return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
   }

   @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String,String>> handleRuntimeException(RuntimeException ex){
       HashMap<String, String> errors = new HashMap<>();
       errors.put("message", ex.getMessage());
       return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
   }

   @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String,String>> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
       HashMap<String, String> errors = new HashMap<>();
       errors.put("message", ex.getMessage());
       return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
   }

   @ExceptionHandler(UserDoesNotExist.class)
    public ResponseEntity<Map<String,String>> handleUserDoesNotExist(UserDoesNotExist ex){
       HashMap<String, String> errors = new HashMap<>();
       errors.put("message", ex.getMessage());
       return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
   }
}
