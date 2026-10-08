package com.textforge.controller;

import com.textforge.service.AlgorithmService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/algorithms")
public class AlgorithmController {

    private final AlgorithmService algorithmService;

    public AlgorithmController(AlgorithmService algorithmService) {
        this.algorithmService = algorithmService;
    }

    @GetMapping("/fibonacci")
    public long fibonacci(@RequestParam int n) {
        return algorithmService.fibonacci(n);
    }

    @GetMapping("/gcd")
    public int gcd(
            @RequestParam int a,
            @RequestParam int b) {

        return algorithmService.gcd(a, b);
    }

    @GetMapping("/prime")
    public boolean isPrime(@RequestParam int n) {
        return algorithmService.isPrime(n);
    }
}