package com.textforge.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DocumentAnalysisService {

    // Count words
    public int countWords(String text) {

        if (text == null || text.trim().isEmpty()) {
            return 0;
        }

        return text.trim().split("\\s+").length;
    }

    // Count characters
    public int countCharacters(String text) {

        if (text == null) {
            return 0;
        }

        return text.length();
    }

    // Count sentences
    public int countSentences(String text) {

        if (text == null || text.trim().isEmpty()) {
            return 0;
        }

        String[] sentences =
                text.trim().split("[.!?]+");

        int count = 0;

        for (String sentence : sentences) {

            if (!sentence.trim().isEmpty()) {
                count++;
            }
        }

        return count;
    }

    // Calculate word frequency
    public Map<String, Integer> wordFrequency(String text) {

        Map<String, Integer> frequency =
                new HashMap<>();

        if (text == null || text.trim().isEmpty()) {
            return frequency;
        }

        String cleanedText =
                text.toLowerCase()
                    .replaceAll("[^a-zA-Z0-9\\s]", " ");

        String[] words =
                cleanedText.trim().split("\\s+");

        for (String word : words) {

            if (!word.isEmpty()) {

                frequency.put(
                        word,
                        frequency.getOrDefault(word, 0) + 1
                );
            }
        }

        return frequency;
    }
}