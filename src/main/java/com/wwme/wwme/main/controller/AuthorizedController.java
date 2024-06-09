package com.wwme.wwme.main.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthorizedController {
    @GetMapping("/my")
    public String myAPI() {
        return "my route";
    }
}
