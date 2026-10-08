package com.textforge.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class TextProcessingService {

    public String cleanText(String text) {

        if (text == null) {
            return "";
        }

        return text
                .replaceAll("\\s+", " ")
                .trim();
    }

    public List<String> tokenize(String text) {

        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String cleanedText = cleanText(text);

        return new ArrayList<>(
                Arrays.asList(cleanedText.split("\\s+"))
        );
    }
}