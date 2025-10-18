package com.yugungsetia.ecommerce_simple.controller;

import com.yugungsetia.ecommerce_simple.common.errors.ForbiddenAccessException;
import com.yugungsetia.ecommerce_simple.model.UserInfo;
import com.yugungsetia.ecommerce_simple.model.UserResponse;
import com.yugungsetia.ecommerce_simple.model.UserUpdateRequest;
import com.yugungsetia.ecommerce_simple.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
@SecurityRequirement(name = "Bearer")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();

        UserResponse userResponse = UserResponse.fromUserAndRoles(userInfo.getUser(), userInfo.getRoles());
        return ResponseEntity.ok(userResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest updateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();

        if (userInfo.getUser().getUserId() != id && !userInfo.getAuthorities().contains("ROLE_ADMIN")) {
            throw new ForbiddenAccessException("user " + userInfo.getUsername() + " tidak memiliki akses untuk update");
        }

        UserResponse updatedUser = userService.updateUser(id, updateDto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponse> deleteUser(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();

        if (userInfo.getUser().getUserId() != id && !userInfo.getAuthorities().contains("ROLE_ADMIN")) {
            throw new ForbiddenAccessException("user " + userInfo.getUsername() + " tidak memiliki akses untuk hapus");
        }

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
