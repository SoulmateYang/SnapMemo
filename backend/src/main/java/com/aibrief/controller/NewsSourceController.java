package com.aibrief.controller;

import com.aibrief.model.NewsSource;
import com.aibrief.service.NewsSourceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sources")
public class NewsSourceController {

    private final NewsSourceService newsSourceService;

    public NewsSourceController(NewsSourceService newsSourceService) {
        this.newsSourceService = newsSourceService;
    }

    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok(newsSourceService.findAll());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody NewsSource source) {
        if (source.getName() == null || source.getName().isBlank()
                || source.getType() == null || source.getUrl() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(201).body(newsSourceService.create(source));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody NewsSource source) {
        return newsSourceService.update(id, source)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return newsSourceService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
