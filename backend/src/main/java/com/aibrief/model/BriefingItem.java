package com.aibrief.model;

import java.time.LocalDateTime;

public class BriefingItem {

    private Long id;
    private Long briefingId;
    private String title;
    private String url;
    private String source;
    private String originalContent;
    private String aiSummary;
    private int importanceScore = 3;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getBriefingId() { return briefingId; }
    public void setBriefingId(Long briefingId) { this.briefingId = briefingId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getOriginalContent() { return originalContent; }
    public void setOriginalContent(String originalContent) { this.originalContent = originalContent; }
    public String getAiSummary() { return aiSummary; }
    public void setAiSummary(String aiSummary) { this.aiSummary = aiSummary; }
    public int getImportanceScore() { return importanceScore; }
    public void setImportanceScore(int importanceScore) { this.importanceScore = importanceScore; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
