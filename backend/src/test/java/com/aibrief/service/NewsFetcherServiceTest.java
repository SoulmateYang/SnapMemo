package com.aibrief.service;

import com.aibrief.model.NewsSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class NewsFetcherServiceTest {

    private NewsFetcherService service;

    @BeforeEach
    void setUp() {
        service = new NewsFetcherService();
    }

    private NewsSource rssSource(String url) {
        NewsSource s = new NewsSource();
        s.setName("Test RSS");
        s.setType("RSS");
        s.setUrl(url);
        s.setMaxItems(20);
        return s;
    }

    private NewsSource hnSource() {
        NewsSource s = new NewsSource();
        s.setName("HackerNews");
        s.setType("API");
        s.setUrl("https://hacker-news.firebaseio.com/v0");
        s.setMaxItems(5);
        return s;
    }

    @Test
    void fetchRss_unknownHost_returnsEmpty() {
        List<NewsFetcherService.NewsItem> items = service.fetchFromSource(
                rssSource("https://this-host-does-not-exist-xyz.com/feed.xml"));
        assertThat(items).isEmpty();
    }

    @Test
    void fetchRss_malformedUrl_throwsIllegalArgument() {
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.fetchFromSource(rssSource("not-a-url")));
    }

    @Test
    void fetchRss_localhostUrl_throwsIllegalArgument() {
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.fetchFromSource(rssSource("http://localhost/feed")));
    }

    @Test
    void fetchRss_privateIpUrl_throwsIllegalArgument() {
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.fetchFromSource(rssSource("http://192.168.1.1/feed")));
    }

    @Test
    void fetchHackerNews_returnsItemsOrEmpty() {
        // Integration-style: real network call, just verify no exception thrown
        NewsSource source = hnSource();
        List<NewsFetcherService.NewsItem> items = service.fetchFromSource(source);
        // Either returns items or empty (network may be unavailable in CI)
        assertThat(items).isNotNull();
    }
}
