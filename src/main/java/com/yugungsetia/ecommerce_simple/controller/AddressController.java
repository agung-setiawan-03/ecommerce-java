package com.yugungsetia.ecommerce_simple.controller;

import com.yugungsetia.ecommerce_simple.model.UserAddressRequest;
import com.yugungsetia.ecommerce_simple.model.UserAddressResponse;
import com.yugungsetia.ecommerce_simple.model.UserInfo;
import com.yugungsetia.ecommerce_simple.service.UserAddressService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("address")
@SecurityRequirement(name = "Bearer")
public class AddressController {

    private final UserAddressService userAddressService;

    @PostMapping("")
    public ResponseEntity<UserAddressResponse> create(@Valid @RequestBody UserAddressRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();

        UserAddressResponse response = userAddressService.create(userInfo.getUser().getUserId(), request);
        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<List<UserAddressResponse>> findAddressByUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();

        List<UserAddressResponse> responses = userAddressService.findByUserId(userInfo.getUser().getUserId());
        return ResponseEntity.ok(responses);
    }


    @GetMapping("/{addressId}")
    public ResponseEntity<UserAddressResponse> get(@PathVariable Long addressId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();

        UserAddressResponse response = userAddressService.findById(addressId);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/{addressId}")
    public ResponseEntity<UserAddressResponse> update(@PathVariable Long addressId, @Valid @RequestBody UserAddressRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();

        UserAddressResponse response = userAddressService.update(addressId, request);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> delete(@PathVariable Long addressId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();

        userAddressService.delete(addressId);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/{addressId}/set-default")
    public ResponseEntity<UserAddressResponse> setDefaultAddress(@PathVariable Long addressId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();

        UserAddressResponse response = userAddressService.setDefaultAddress(userInfo.getUser().getUserId(), addressId);
        return ResponseEntity.ok(response);
    }
}
