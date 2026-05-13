package com.aibrief.service;

import com.aibrief.mapper.BriefingItemMapper;
import com.aibrief.mapper.BriefingMapper;
import com.aibrief.mapper.NewsSourceMapper;
import com.aibrief.model.Briefing;
import com.aibrief.model.Briefing.BriefingStatus;
import com.aibrief.model.NewsSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BriefingServiceTest {

    @Mock private BriefingMapper briefingMapper;
    @Mock private BriefingItemMapper briefingItemMapper;
    @Mock private NewsSourceMapper newsSourceMapper;
    @Mock private NewsFetcherService newsFetcherService;
    @Mock private AISummarizerService aiSummarizerService;

    private BriefingService service;

    @BeforeEach
    void setUp() {
        service = new BriefingService(briefingMapper, briefingItemMapper, newsSourceMapper,
                newsFetcherService, aiSummarizerService);
    }

    private NewsSource enabledSource(String name) {
        NewsSource s = new NewsSource();
        s.setName(name);
        s.setType("RSS");
        s.setUrl("https://example.com/feed");
        s.setEnabled(true);
        s.setMaxItems(20);
        return s;
    }

    private Briefing briefingWithStatus(BriefingStatus status) {
        Briefing b = new Briefing();
        b.setId(1L);
        b.setDate(LocalDate.now());
        b.setStatus(status);
        b.setTitle(LocalDate.now() + " AI 简报");
        return b;
    }

    @Test
    void generate_existingDone_skips() {
        when(briefingMapper.findByDate(any())).thenReturn(Optional.of(briefingWithStatus(BriefingStatus.DONE)));

        Briefing result = service.generate();

        assertThat(result.getStatus()).isEqualTo(BriefingStatus.DONE);
        verify(newsSourceMapper, never()).findByEnabledTrue();
    }

    @Test
    void generate_existingGenerating_skips() {
        when(briefingMapper.findByDate(any())).thenReturn(Optional.of(briefingWithStatus(BriefingStatus.GENERATING)));

        service.generate();

        verify(newsSourceMapper, never()).findByEnabledTrue();
    }

    @Test
    void generate_existingFailed_regenerates() {
        when(briefingMapper.findByDate(any())).thenReturn(Optional.of(briefingWithStatus(BriefingStatus.FAILED)));
        when(newsSourceMapper.findByEnabledTrue()).thenReturn(List.of());

        Briefing result = service.generate();

        assertThat(result.getStatus()).isEqualTo(BriefingStatus.FAILED);
        verify(newsSourceMapper).findByEnabledTrue();
    }

    @Test
    void generate_allSourcesFail_statusFailed() {
        when(briefingMapper.findByDate(any())).thenReturn(Optional.empty());
        when(newsSourceMapper.findByEnabledTrue()).thenReturn(List.of(enabledSource("S1")));
        when(newsFetcherService.fetchFromSource(any())).thenReturn(List.of());

        Briefing result = service.generate();

        assertThat(result.getStatus()).isEqualTo(BriefingStatus.FAILED);
    }

    @Test
    void generate_someSourcesFail_statusPartial() {
        when(briefingMapper.findByDate(any())).thenReturn(Optional.empty());
        when(newsSourceMapper.findByEnabledTrue())
                .thenReturn(List.of(enabledSource("S1"), enabledSource("S2")));
        when(newsFetcherService.fetchFromSource(any()))
                .thenReturn(List.of(new NewsFetcherService.NewsItem("T", "u", "c", "S1")))
                .thenReturn(List.of());
        when(aiSummarizerService.summarize(any(), any()))
                .thenReturn(new AISummarizerService.SummaryResult("summary", 3));

        Briefing result = service.generate();

        assertThat(result.getStatus()).isEqualTo(BriefingStatus.PARTIAL);
    }

    @Test
    void generate_allSourcesSuccess_statusDone() {
        when(briefingMapper.findByDate(any())).thenReturn(Optional.empty());
        when(newsSourceMapper.findByEnabledTrue()).thenReturn(List.of(enabledSource("S1")));
        when(newsFetcherService.fetchFromSource(any()))
                .thenReturn(List.of(new NewsFetcherService.NewsItem("T", "u", "c", "S1")));
        when(aiSummarizerService.summarize(any(), any()))
                .thenReturn(new AISummarizerService.SummaryResult("summary", 4));

        Briefing result = service.generate();

        assertThat(result.getStatus()).isEqualTo(BriefingStatus.DONE);
    }

    @Test
    void generate_disabledSource_skipped() {
        when(briefingMapper.findByDate(any())).thenReturn(Optional.empty());
        when(newsSourceMapper.findByEnabledTrue()).thenReturn(List.of());

        service.generate();

        verify(newsFetcherService, never()).fetchFromSource(any());
    }

    @Test
    void generate_maxItemsTruncated() {
        NewsSource source = enabledSource("S1");
        source.setMaxItems(1);
        when(briefingMapper.findByDate(any())).thenReturn(Optional.empty());
        when(newsSourceMapper.findByEnabledTrue()).thenReturn(List.of(source));
        when(newsFetcherService.fetchFromSource(any()))
                .thenReturn(List.of(new NewsFetcherService.NewsItem("T", "u", "c", "S1")));
        when(aiSummarizerService.summarize(any(), any()))
                .thenReturn(new AISummarizerService.SummaryResult("s", 3));

        Briefing result = service.generate();

        assertThat(result.getItems()).hasSize(1);
    }
}
