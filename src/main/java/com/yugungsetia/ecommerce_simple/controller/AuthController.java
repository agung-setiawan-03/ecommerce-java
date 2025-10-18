package com.yugungsetia.ecommerce_simple.controller;

import com.yugungsetia.ecommerce_simple.model.*;
import com.yugungsetia.ecommerce_simple.service.AuthService;
import com.yugungsetia.ecommerce_simple.service.JwtService;
import com.yugungsetia.ecommerce_simple.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final JwtService jwtService;


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest authRequest) {
        UserInfo userInfo = authService.authenticate(authRequest);
        String token = jwtService.generateToken(userInfo);
        AuthResponse authResponse = AuthResponse.fromUserInfo(userInfo, token);

        return ResponseEntity.ok(authResponse);
    }


    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRegisterRequest registerRequest) {
        UserResponse userResponse = userService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userResponse);
    }

}
