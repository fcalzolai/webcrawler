package com.webcrawler.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IsActiveController {

    private static final String IS_ACTIVE = "ACTIVE";

    @GetMapping("/isActive")
    public String index() {
        return IS_ACTIVE;
    }
}