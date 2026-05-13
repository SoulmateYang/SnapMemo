package com.aibrief.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class AISummarizerServiceTest {

    private AISummarizerService service;

    @BeforeEach
    void setUp() {
        service = new AISummarizerService(5000, 30000);
        ReflectionTestUtils.setField(service, "apiKey", "");
        ReflectionTestUtils.setField(service, "apiUrl", "https://api.openai.com/v1/chat/completions");
        ReflectionTestUtils.setField(service, "model", "gpt-4o-mini");
        ReflectionTestUtils.setField(service, "requestDelayMs", 0L);
    }

    @Test
    void noApiKey_returnsFallback() {
        AISummarizerService.SummaryResult result = service.summarize("Test Title", "content");
        assertThat(result.summary()).isEqualTo("Test Title");
        assertThat(result.importanceScore()).isEqualTo(3);
    }

    @Test
    void importanceScore_clampsAbove5() {
        // Test clamp logic directly via reflection
        var method = org.springframework.test.util.ReflectionTestUtils.invokeMethod(
                service, "clamp", 7);
        assertThat(method).isEqualTo(5);
    }

    @Test
    void importanceScore_clampsBelowOne() {
        var method = org.springframework.test.util.ReflectionTestUtils.invokeMethod(
                service, "clamp", -1);
        assertThat(method).isEqualTo(1);
    }

    @Test
    void importanceScore_validValueUnchanged() {
        var method = org.springframework.test.util.ReflectionTestUtils.invokeMethod(
                service, "clamp", 4);
        assertThat(method).isEqualTo(4);
    }

    @Test
    void parseContent_nonJson_returnsFallback() {
        var method = org.springframework.test.util.ReflectionTestUtils.invokeMethod(
                service, "parseContent", "This is plain text, not JSON", "My Title");
        assertThat(method).isInstanceOf(AISummarizerService.SummaryResult.class);
        AISummarizerService.SummaryResult result = (AISummarizerService.SummaryResult) method;
        assertThat(result.summary()).isEqualTo("My Title");
        assertThat(result.importanceScore()).isEqualTo(3);
    }

    @Test
    void parseContent_validJson_parsed() {
        var method = org.springframework.test.util.ReflectionTestUtils.invokeMethod(
                service, "parseContent", "{\"summary\":\"Good summary\",\"importance\":4}", "My Title");
        AISummarizerService.SummaryResult result = (AISummarizerService.SummaryResult) method;
        assertThat(result.summary()).isEqualTo("Good summary");
        assertThat(result.importanceScore()).isEqualTo(4);
    }

    @Test
    void parseContent_missingSummaryField_returnsFallback() {
        var method = org.springframework.test.util.ReflectionTestUtils.invokeMethod(
                service, "parseContent", "{\"importance\":3}", "My Title");
        AISummarizerService.SummaryResult result = (AISummarizerService.SummaryResult) method;
        assertThat(result.summary()).isEqualTo("My Title");
    }

    @Test
    void parseContent_missingImportanceField_defaultsTo3() {
        var method = org.springframework.test.util.ReflectionTestUtils.invokeMethod(
                service, "parseContent", "{\"summary\":\"Some summary\"}", "My Title");
        AISummarizerService.SummaryResult result = (AISummarizerService.SummaryResult) method;
        assertThat(result.importanceScore()).isEqualTo(3);
    }

    @Test
    void parseContent_markdownCodeBlock_parsed() {
        String content = "```json\n{\"summary\":\"Good summary\",\"importance\":5}\n```";
        var method = org.springframework.test.util.ReflectionTestUtils.invokeMethod(
                service, "parseContent", content, "My Title");
        AISummarizerService.SummaryResult result = (AISummarizerService.SummaryResult) method;
        assertThat(result.summary()).isEqualTo("Good summary");
        assertThat(result.importanceScore()).isEqualTo(5);
    }
}
