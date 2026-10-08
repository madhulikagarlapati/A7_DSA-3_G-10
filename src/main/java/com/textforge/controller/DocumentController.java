package com.textforge.controller;

import com.textforge.model.Document;
import com.textforge.service.DocumentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping
    public List<Document> getAllDocuments() {
        return documentService.getAllDocuments();
    }

    @PostMapping
    public Document addDocument(@RequestBody Document document) {
        return documentService.addDocument(document);
    }
}