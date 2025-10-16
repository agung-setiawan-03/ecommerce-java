package com.yugungsetia.ecommerce_simple.controller;

import com.yugungsetia.ecommerce_simple.common.errors.BadRequestException;
import com.yugungsetia.ecommerce_simple.common.errors.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello World";
    }

    @GetMapping("/generic-error")
    public ResponseEntity<String> genericError() {
        throw new RuntimeException("Generic error");
    }

    @GetMapping("/not-found")
    public ResponseEntity<String> notFoundError() {
        throw new ResourceNotFoundException("Data tidak ditemukan");
    }


    @GetMapping("/badrequest-error")
    public ResponseEntity<String> badRequestError() {
        throw new BadRequestException("Terjadi kesalahan pada client");
    }
}
