package com.aibrief.service;

import com.aibrief.model.NewsSource;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class NewsFetcherService {

    private static final Logger log = LoggerFactory.getLogger(NewsFetcherService.class);

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build();

    public record NewsItem(String title, String url, String content, String source) {}

    public List<NewsItem> fetchFromSource(NewsSource source) {
        validateUrl(source.getUrl());
        return switch (source.getType().toUpperCase()) {
            case "RSS" -> fetchRss(source);
            case "API" -> fetchHackerNews(source);
            default -> {
                log.warn("Unknown source type: {}", source.getType());
                yield List.of();
            }
        };
    }

    private List<NewsItem> fetchRss(NewsSource source) {
        try {
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(new URL(source.getUrl())));
            List<SyndEntry> entries = feed.getEntries();
            int limit = Math.min(entries.size(), source.getMaxItems());
            List<NewsItem> items = new ArrayList<>(limit);
            for (int i = 0; i < limit; i++) {
                SyndEntry entry = entries.get(i);
                String content = entry.getDescription() != null ? entry.getDescription().getValue() : "";
                items.add(new NewsItem(entry.getTitle(), entry.getLink(),
                        truncate(content, 500), source.getName()));
            }
            return items;
        } catch (UnknownHostException e) {
            log.warn("Unknown host for source {}: {}", source.getName(), e.getMessage());
            return List.of();
        } catch (FeedException e) {
            log.warn("Malformed RSS for source {}: {}", source.getName(), e.getMessage());
            return List.of();
        } catch (IOException e) {
            log.warn("Network error for source {}: {}", source.getName(), e.getMessage());
            return List.of();
        }
    }

    private List<NewsItem> fetchHackerNews(NewsSource source) {
        try {
            String baseUrl = source.getUrl();
            Request topStoriesReq = new Request.Builder()
                    .url(baseUrl + "/topstories.json")
                    .build();
            try (Response response = httpClient.newCall(topStoriesReq).execute()) {
                if (!response.isSuccessful() || response.body() == null) return List.of();
                String body = response.body().string();
                // Parse JSON array of IDs
                body = body.trim().replaceAll("[\\[\\]\\s]", "");
                if (body.isEmpty()) return List.of();
                String[] ids = body.split(",");
                int limit = Math.min(ids.length, source.getMaxItems());
                List<NewsItem> items = new ArrayList<>(limit);
                for (int i = 0; i < limit; i++) {
                    NewsItem item = fetchHackerNewsItem(baseUrl, ids[i].trim());
                    if (item != null) items.add(item);
                }
                return items;
            }
        } catch (IOException e) {
            log.warn("Error fetching HackerNews: {}", e.getMessage());
            return List.of();
        }
    }

    private NewsItem fetchHackerNewsItem(String baseUrl, String id) {
        try {
            Request req = new Request.Builder()
                    .url(baseUrl + "/item/" + id + ".json")
                    .build();
            try (Response response = httpClient.newCall(req).execute()) {
                if (!response.isSuccessful() || response.body() == null) return null;
                String body = response.body().string();
                String title = extractJsonField(body, "title");
                String url = extractJsonField(body, "url");
                if (title == null || title.isBlank()) return null;
                return new NewsItem(title, url != null ? url : "https://news.ycombinator.com/item?id=" + id,
                        "", "HackerNews");
            }
        } catch (IOException e) {
            return null;
        }
    }

    private void validateUrl(String url) {
        try {
            URL parsed = new URL(url);
            String protocol = parsed.getProtocol();
            String host = parsed.getHost();
            if (!protocol.equals("http") && !protocol.equals("https")) {
                throw new IllegalArgumentException("Invalid protocol: " + protocol);
            }
            if (host.equals("localhost") || host.startsWith("127.") ||
                    host.startsWith("192.168.") || host.startsWith("169.254.")) {
                throw new IllegalArgumentException("SSRF blocked: " + host);
            }
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL: " + url);
        }
    }

    private String truncate(String text, int maxLen) {
        if (text == null) return "";
        // Strip HTML tags
        text = text.replaceAll("<[^>]+>", "").trim();
        return text.length() > maxLen ? text.substring(0, maxLen) : text;
    }

    // Minimal JSON field extractor (avoids extra dependency for simple cases)
    private String extractJsonField(String json, String field) {
        String key = "\"" + field + "\"";
        int idx = json.indexOf(key);
        if (idx < 0) return null;
        int colon = json.indexOf(':', idx + key.length());
        if (colon < 0) return null;
        int start = json.indexOf('"', colon + 1);
        if (start < 0) return null;
        int end = json.indexOf('"', start + 1);
        if (end < 0) return null;
        return json.substring(start + 1, end);
    }
}
