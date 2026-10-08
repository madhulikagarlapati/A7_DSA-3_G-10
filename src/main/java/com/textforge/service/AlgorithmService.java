package com.textforge.service;

import org.springframework.stereotype.Service;

@Service
public class AlgorithmService {

    // Fibonacci using Dynamic Programming
    public long fibonacci(int n) {

        if (n < 0) {
            return -1;
        }

        if (n <= 1) {
            return n;
        }

        long previous = 0;
        long current = 1;

        for (int i = 2; i <= n; i++) {

            long next = previous + current;

            previous = current;
            current = next;
        }

        return current;
    }

    // Greatest Common Divisor using Euclidean Algorithm
    public int gcd(int a, int b) {

        a = Math.abs(a);
        b = Math.abs(b);

        while (b != 0) {

            int remainder = a % b;

            a = b;
            b = remainder;
        }

        return a;
    }

    // Check whether a number is prime
    public boolean isPrime(int n) {

        if (n < 2) {
            return false;
        }

        if (n == 2) {
            return true;
        }

        if (n % 2 == 0) {
            return false;
        }

        for (int i = 3; i <= n / i; i += 2) {

            if (n % i == 0) {
                return false;
            }
        }

        return true;
    }
}