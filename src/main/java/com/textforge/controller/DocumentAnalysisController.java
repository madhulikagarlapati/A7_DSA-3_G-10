package com.textforge.controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.textforge.service.DocumentAnalysisService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/analysis")
public class DocumentAnalysisController {

    private final DocumentAnalysisService analysisService;

    public DocumentAnalysisController(
            DocumentAnalysisService analysisService) {

        this.analysisService = analysisService;
    }

    // Word Count
    @GetMapping("/word-count")
    public int wordCount(@RequestParam String text) {

        return analysisService.countWords(text);
    }

    // Character Count
    @GetMapping("/character-count")
    public int characterCount(@RequestParam String text) {

        return analysisService.countCharacters(text);
    }

    // Sentence Count
    @GetMapping("/sentence-count")
    public int sentenceCount(@RequestParam String text) {

        return analysisService.countSentences(text);
    }

    // Word Frequency
    @GetMapping("/word-frequency")
    public Map<String, Integer> wordFrequency(
            @RequestParam String text) {

        return analysisService.wordFrequency(text);
    }
}