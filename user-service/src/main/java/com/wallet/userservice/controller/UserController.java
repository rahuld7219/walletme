package com.wallet.userservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wallet.userservice.request.UserCreationRequestDTO;
import com.wallet.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Long> createUser(@RequestBody UserCreationRequestDTO userCreationRequestDTO) throws JsonProcessingException, ExecutionException, InterruptedException {
        Long userId = userService.createUser(userCreationRequestDTO);
        return ResponseEntity.ok(userId);
    }
}
