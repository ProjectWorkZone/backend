package com.project.workzone.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/api/v1/test")
    public String test(){
        return "Test";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/api/v1/admin-test")
    public String adminTest() {
        return "Admin Test";
    }
}
