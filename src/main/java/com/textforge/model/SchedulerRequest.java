package com.textforge.model;

import java.util.ArrayList;
import java.util.List;

public class SchedulerRequest {

    private String document;
    private List<String> additionalOperations = new ArrayList<>();
    private String searchPattern;
    private int processors = 4;

    public SchedulerRequest() {
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public List<String> getAdditionalOperations() {
        return additionalOperations;
    }

    public void setAdditionalOperations(
            List<String> additionalOperations) {

        this.additionalOperations =
                additionalOperations;
    }

    public String getSearchPattern() {
        return searchPattern;
    }

    public void setSearchPattern(String searchPattern) {
        this.searchPattern = searchPattern;
    }

    public int getProcessors() {
        return processors;
    }

    public void setProcessors(int processors) {
        this.processors = processors;
    }
}