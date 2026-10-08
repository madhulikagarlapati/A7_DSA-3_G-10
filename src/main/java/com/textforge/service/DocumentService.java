package com.textforge.service;

import com.textforge.model.Document;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentService {

    private final List<Document> documents = new ArrayList<>();

    public List<Document> getAllDocuments() {
        return documents;
    }

    public Document addDocument(Document document) {
        document.setId((long) (documents.size() + 1));
        documents.add(document);
        return document;
    }
}