package com.yzc.bankingapp.controller;

import com.yzc.bankingapp.model.CreateUserReq;
import com.yzc.bankingapp.model.LoginUserReq;
import com.yzc.bankingapp.service.UserService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Api(value = "User Controller", tags = "User Process Controller")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/register")
    @ApiOperation(value = "Register User", notes = "This endpoint written for create a new user ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "requestData", value = "Request Data", paramType = "body",
                    defaultValue = "{\"propertyName\": \"exampleValue\"}")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User created succesfully"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 401, message = "User already exists")
    })
    public ResponseEntity<?> registerUser (@RequestBody CreateUserReq createUserReq){
       // Registers a new user with username, password, and email.
        return userService.registerUser(createUserReq);
    }

    @PostMapping("/login")
    public ResponseEntity<?> userLogin (@RequestBody LoginUserReq loginUserReq){
      //  Authenticates a user and returns a JWT for accessing protected endpoints.
        return userService.userLogin(loginUserReq);
    }

    @GetMapping("/test")
    public ResponseEntity<?> test (){
        //  Authenticates a user and returns a JWT for accessing protected endpoints.
        return ResponseEntity.status(200).body("abc");
    }

}
