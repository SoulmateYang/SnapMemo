package com.aibrief.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Briefing {

    public enum BriefingStatus { GENERATING, DONE, PARTIAL, FAILED }

    private Long id;
    private LocalDate date;
    private String title;
    private String summary;
    private BriefingStatus status;
    private LocalDateTime createdAt;
    private List<BriefingItem> items = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public BriefingStatus getStatus() { return status; }
    public void setStatus(BriefingStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<BriefingItem> getItems() { return items; }
    public void setItems(List<BriefingItem> items) { this.items = items; }
}
