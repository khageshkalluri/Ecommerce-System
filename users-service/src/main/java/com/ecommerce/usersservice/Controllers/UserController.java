package com.ecommerce.usersservice.Controllers;


import com.ecommerce.usersservice.Dto.UpdateUserValidation;
import com.ecommerce.usersservice.Dto.UserPatchRequest;
import com.ecommerce.usersservice.Dto.UserRequest;
import com.ecommerce.usersservice.Dto.UsersDto;
import com.ecommerce.usersservice.Service.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Users",description = "API to Perform CRUD Operations for Users")
@Validated
public class UserController {

    private UserServiceImpl userService;
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/getUsers")
    @Operation(description = "API to Get All Users",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = false)
    ,responses = {
            @ApiResponse(
                    description = "Get Request Response",
                    responseCode = "200",
                    content=@Content(
                            schema=@Schema(implementation=UsersDto.class)
                    )
            )
    })
    public ResponseEntity<List<UsersDto>> getAllUsers() {
       return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @PostMapping("/addUsers")
    @Operation(description = "API to Add a new User",requestBody =@io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        description = "POST request body",
        content = @Content(
                    schema = @Schema(implementation = UserRequest.class)
            )
    ),responses = {
            @ApiResponse(
                    description = "POST API response",
                    responseCode = "201",
                    content =  @Content(
                            schema = @Schema(implementation = UsersDto.class)
                    )
            )
    })
    public ResponseEntity<UsersDto> addUser(@Valid @RequestBody UserRequest userRequest) {
        return new ResponseEntity<>(userService.addUser(userRequest),HttpStatus.CREATED);
    }

    @PutMapping("/updateUsers")
    @Operation(description = "API to update a User",requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "PUT request body",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = UserRequest.class)
            )
    ),responses = {
            @ApiResponse(
                    description = "PUT API Response",
                    responseCode = "202",
                    content =  @Content(
                            schema = @Schema(implementation = UsersDto.class)
                    )
            )
    })
    public ResponseEntity<UsersDto> updateUser(@RequestBody @Validated({Default.class, UpdateUserValidation.class}) UserRequest userRequest) {
     return new ResponseEntity<>(userService.updateUser(userRequest),HttpStatus.ACCEPTED);
    }

    @PatchMapping("/patchUsers")
    @Operation(description = "API to Patch a User",requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "PATCH API Request schema",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = UserPatchRequest.class)
            )
    ),responses = {
            @ApiResponse(
                    description = "PATCH Response",
                    responseCode = "202",
                    content =  @Content(
                            schema = @Schema(implementation = UsersDto.class)
                    )
            )
    })
    public ResponseEntity<UsersDto> patchUser(@Valid @RequestBody UserPatchRequest userRequest) {
   return new ResponseEntity<>(userService.patchUsers(userRequest),HttpStatus.ACCEPTED);
    }

    @GetMapping("/{email}")
    @Operation(description = "API to get a Specific User based on email Address",responses = {
            @ApiResponse(
                    description = "Get Request Response",
                    responseCode = "200",
                    content=@Content(
                            schema=@Schema(implementation=UsersDto.class)
                    )
            )
    } )
    public ResponseEntity<UsersDto> getUser(@PathVariable("email") String email) {
        return new ResponseEntity<>(userService.getUserByEmail(email),HttpStatus.OK);
    }

    @GetMapping("/{name}/get")
    @Operation(description = "API to get Users based on User Name",responses = {
            @ApiResponse(
                    description = "Get Request Response",
                    responseCode = "200",
                    content=@Content(
                            schema=@Schema(implementation=UsersDto.class)
                    )
            )
    })
    public ResponseEntity<List<UsersDto>> getUserByName(@PathVariable("name") String name) {
        return new ResponseEntity<>(userService.getUserByUsername(name),HttpStatus.OK);
    }
    @DeleteMapping("/{email}/delete")
    @Operation(description = "API to delete a User based on email Address")
    public ResponseEntity<Void> deleteUser(@PathVariable("email") String email) {
        this.userService.deleteUserByEmail(email);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
