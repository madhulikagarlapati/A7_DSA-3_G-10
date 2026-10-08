package com.textforge.service;

import org.springframework.stereotype.Service;

@Service
public class SearchService {

    // Naive String Search
    public int naiveSearch(String text, String pattern) {

        if (text == null || pattern == null) {
            return -1;
        }

        if (pattern.isEmpty()) {
            return 0;
        }

        for (int i = 0; i <= text.length() - pattern.length(); i++) {

            int j = 0;

            while (j < pattern.length()
                    && text.charAt(i + j) == pattern.charAt(j)) {

                j++;
            }

            if (j == pattern.length()) {
                return i;
            }
        }

        return -1;
    }

    // KMP String Search
    public int kmpSearch(String text, String pattern) {

        if (text == null || pattern == null) {
            return -1;
        }

        if (pattern.isEmpty()) {
            return 0;
        }

        int[] lps = buildLps(pattern);

        int i = 0;
        int j = 0;

        while (i < text.length()) {

            if (text.charAt(i) == pattern.charAt(j)) {

                i++;
                j++;

                if (j == pattern.length()) {
                    return i - j;
                }

            } else if (j > 0) {

                j = lps[j - 1];

            } else {

                i++;
            }
        }

        return -1;
    }

    // Build LPS array for KMP
    private int[] buildLps(String pattern) {

        int[] lps = new int[pattern.length()];

        int length = 0;
        int i = 1;

        while (i < pattern.length()) {

            if (pattern.charAt(i) == pattern.charAt(length)) {

                length++;
                lps[i] = length;
                i++;

            } else if (length > 0) {

                length = lps[length - 1];

            } else {

                lps[i] = 0;
                i++;
            }
        }

        return lps;
    }

    // Z Algorithm String Search
    public int zSearch(String text, String pattern) {

        if (text == null || pattern == null) {
            return -1;
        }

        if (pattern.isEmpty()) {
            return 0;
        }

        if (pattern.length() > text.length()) {
            return -1;
        }

        String combined = pattern + "$" + text;

        int[] z = new int[combined.length()];

        int left = 0;
        int right = 0;

        for (int i = 1; i < combined.length(); i++) {

            if (i <= right) {
                z[i] = Math.min(
                        right - i + 1,
                        z[i - left]
                );
            }

            while (i + z[i] < combined.length()
                    && combined.charAt(z[i])
                    == combined.charAt(i + z[i])) {

                z[i]++;
            }

            if (i + z[i] - 1 > right) {

                left = i;
                right = i + z[i] - 1;
            }

            if (z[i] == pattern.length()) {
                return i - pattern.length() - 1;
            }
        }

        return -1;
    }

    // Rabin-Karp String Search
    public int rabinKarpSearch(String text, String pattern) {

        if (text == null || pattern == null) {
            return -1;
        }

        if (pattern.isEmpty()) {
            return 0;
        }

        if (pattern.length() > text.length()) {
            return -1;
        }

        int base = 256;
        int prime = 101;

        int patternHash = 0;
        int textHash = 0;
        int highestPower = 1;

        for (int i = 0; i < pattern.length() - 1; i++) {
            highestPower =
                    (highestPower * base) % prime;
        }

        for (int i = 0; i < pattern.length(); i++) {

            patternHash =
                    (base * patternHash
                    + pattern.charAt(i)) % prime;

            textHash =
                    (base * textHash
                    + text.charAt(i)) % prime;
        }

        for (int i = 0;
             i <= text.length() - pattern.length();
             i++) {

            if (patternHash == textHash) {

                int j = 0;

                while (j < pattern.length()
                        && text.charAt(i + j)
                        == pattern.charAt(j)) {

                    j++;
                }

                if (j == pattern.length()) {
                    return i;
                }
            }

            if (i < text.length() - pattern.length()) {

                textHash =
                        (base * (
                                textHash
                                - text.charAt(i) * highestPower
                        )
                        + text.charAt(i + pattern.length()))
                        % prime;

                if (textHash < 0) {
                    textHash += prime;
                }
            }
        }

        return -1;
    }
}