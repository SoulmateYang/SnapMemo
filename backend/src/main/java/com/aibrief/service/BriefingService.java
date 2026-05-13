package com.aibrief.service;

import com.aibrief.mapper.BriefingItemMapper;
import com.aibrief.mapper.BriefingMapper;
import com.aibrief.mapper.NewsSourceMapper;
import com.aibrief.model.Briefing;
import com.aibrief.model.Briefing.BriefingStatus;
import com.aibrief.model.BriefingItem;
import com.aibrief.model.NewsSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BriefingService {

    private static final Logger log = LoggerFactory.getLogger(BriefingService.class);

    private final BriefingMapper briefingMapper;
    private final BriefingItemMapper briefingItemMapper;
    private final NewsSourceMapper newsSourceMapper;
    private final NewsFetcherService newsFetcherService;
    private final AISummarizerService aiSummarizerService;

    public BriefingService(BriefingMapper briefingMapper,
                           BriefingItemMapper briefingItemMapper,
                           NewsSourceMapper newsSourceMapper,
                           NewsFetcherService newsFetcherService,
                           AISummarizerService aiSummarizerService) {
        this.briefingMapper = briefingMapper;
        this.briefingItemMapper = briefingItemMapper;
        this.newsSourceMapper = newsSourceMapper;
        this.newsFetcherService = newsFetcherService;
        this.aiSummarizerService = aiSummarizerService;
    }

    public Map<String, Object> findAll(int page, int size) {
        int offset = (page - 1) * size;
        List<Briefing> items = briefingMapper.findAll(offset, size);
        long total = briefingMapper.countAll();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("items", items);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return result;
    }

    public Optional<Briefing> findById(Long id) {
        return briefingMapper.findById(id);
    }

    public Optional<Briefing> getTodayBriefing() {
        return briefingMapper.findByDateWithItems(LocalDate.now());
    }

    @Transactional
    public Briefing generate() {
        LocalDate today = LocalDate.now();
        Optional<Briefing> existing = briefingMapper.findByDate(today);
        if (existing.isPresent()) {
            BriefingStatus status = existing.get().getStatus();
            if (status == BriefingStatus.DONE || status == BriefingStatus.GENERATING) {
                log.info("Briefing for {} already exists with status {}, skipping", today, status);
                return existing.get();
            }
        }

        Briefing briefing = existing.orElseGet(() -> {
            Briefing b = new Briefing();
            b.setDate(today);
            b.setTitle(today + " AI 简报");
            b.setStatus(BriefingStatus.GENERATING);
            briefingMapper.insert(b);
            return b;
        });
        briefing.setStatus(BriefingStatus.GENERATING);
        briefingMapper.update(briefing);

        List<NewsSource> sources = newsSourceMapper.findByEnabledTrue();
        int successCount = 0;
        int failCount = 0;

        for (NewsSource source : sources) {
            try {
                int count = processSource(briefing, source);
                if (count > 0) successCount++;
                else failCount++;
            } catch (Exception e) {
                log.error("Failed to process source {}: {}", source.getName(), e.getMessage());
                failCount++;
            }
        }

        int totalItems = briefing.getItems().size();
        briefing.setSummary("共 " + totalItems + " 条 AI 新闻");
        if (successCount == 0) briefing.setStatus(BriefingStatus.FAILED);
        else if (failCount > 0) briefing.setStatus(BriefingStatus.PARTIAL);
        else briefing.setStatus(BriefingStatus.DONE);
        briefingMapper.update(briefing);
        return briefing;
    }

    private int processSource(Briefing briefing, NewsSource source) {
        List<NewsFetcherService.NewsItem> items = newsFetcherService.fetchFromSource(source);
        if (items.isEmpty()) return 0;
        for (NewsFetcherService.NewsItem newsItem : items) {
            BriefingItem item = buildItem(briefing.getId(), newsItem);
            briefingItemMapper.insert(item);
            briefing.getItems().add(item);
        }
        return items.size();
    }

    private BriefingItem buildItem(Long briefingId, NewsFetcherService.NewsItem newsItem) {
        AISummarizerService.SummaryResult result =
                aiSummarizerService.summarize(newsItem.title(), newsItem.content());
        BriefingItem item = new BriefingItem();
        item.setBriefingId(briefingId);
        item.setTitle(newsItem.title());
        item.setUrl(newsItem.url());
        item.setSource(newsItem.source());
        item.setOriginalContent(newsItem.content());
        if (!result.summary().equals(newsItem.title())) {
            item.setAiSummary(result.summary());
        }
        item.setImportanceScore(result.importanceScore());
        return item;
    }
}
