package com.aibrief.controller;

import com.aibrief.model.Briefing;
import com.aibrief.service.BriefingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/briefings")
public class BriefingController {

    private final BriefingService briefingService;

    @Value("${briefing.trigger-token}")
    private String triggerToken;

    public BriefingController(BriefingService briefingService) {
        this.briefingService = briefingService;
    }

    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(briefingService.findAll(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return briefingService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/today")
    public ResponseEntity<?> getToday() {
        return briefingService.getTodayBriefing()
                .map(briefing -> ResponseEntity.ok(Map.of("exists", true, "data", briefing)))
                .orElseGet(() -> {
                    Map<String, Object> body = new HashMap<>();
                    body.put("exists", false);
                    body.put("data", null);
                    return ResponseEntity.ok(body);
                });
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestHeader(value = "X-Trigger-Token", required = false) String token) {
        if (!triggerToken.equals(token)) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        Briefing briefing = briefingService.generate();
        return ResponseEntity.ok(briefing);
    }
}
