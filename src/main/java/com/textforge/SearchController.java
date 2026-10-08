package com.textforge;

import com.textforge.service.SearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/api/search/naive")
    public int naiveSearch(
            @RequestParam String text,
            @RequestParam String pattern) {

        return searchService.naiveSearch(text, pattern);
    }

    @GetMapping("/api/search/kmp")
    public int kmpSearch(
            @RequestParam String text,
            @RequestParam String pattern) {

        return searchService.kmpSearch(text, pattern);
    }

    @GetMapping("/api/search/z")
    public int zSearch(
            @RequestParam String text,
            @RequestParam String pattern) {

        return searchService.zSearch(text, pattern);
    }

    @GetMapping("/api/search/rabin-karp")
    public int rabinKarpSearch(
            @RequestParam String text,
            @RequestParam String pattern) {

        return searchService.rabinKarpSearch(text, pattern);
    }
}