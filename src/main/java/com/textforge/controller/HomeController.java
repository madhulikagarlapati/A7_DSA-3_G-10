package com.textforge.controller;

import com.textforge.service.TextforgeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    private final TextforgeService textforgeService;

    public HomeController(TextforgeService textforgeService) {
        this.textforgeService = textforgeService;
    }

    @GetMapping("/")
    public String home() {
        return textforgeService.getWelcomeMessage();
    }
}