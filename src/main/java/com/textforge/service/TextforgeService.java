package com.textforge.service;

import org.springframework.stereotype.Service;

@Service
public class TextforgeService {

    public String getWelcomeMessage() {
        return "TEXTFORGE is running!";
    }
}