package com.aibrief.controller;

import com.aibrief.model.Briefing;
import com.aibrief.model.Briefing.BriefingStatus;
import com.aibrief.service.BriefingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BriefingController.class)
class BriefingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BriefingService briefingService;

    private Briefing briefing(BriefingStatus status) {
        Briefing b = new Briefing();
        b.setDate(LocalDate.now());
        b.setTitle("Test Briefing");
        b.setStatus(status);
        return b;
    }

    @Test
    void getToday_withDoneBriefing_returns200() throws Exception {
        when(briefingService.getTodayBriefing()).thenReturn(Optional.of(briefing(BriefingStatus.DONE)));

        mockMvc.perform(get("/api/briefings/today"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(true))
                .andExpect(jsonPath("$.data.status").value("DONE"));
    }

    @Test
    void getToday_noBriefing_returns200WithExistsFalse() throws Exception {
        when(briefingService.getTodayBriefing()).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/briefings/today"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(false))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void getToday_generatingBriefing_returns200() throws Exception {
        when(briefingService.getTodayBriefing()).thenReturn(Optional.of(briefing(BriefingStatus.GENERATING)));

        mockMvc.perform(get("/api/briefings/today"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(true))
                .andExpect(jsonPath("$.data.status").value("GENERATING"));
    }

    @Test
    void getToday_failedBriefing_returns200() throws Exception {
        when(briefingService.getTodayBriefing()).thenReturn(Optional.of(briefing(BriefingStatus.FAILED)));

        mockMvc.perform(get("/api/briefings/today"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(true))
                .andExpect(jsonPath("$.data.status").value("FAILED"));
    }

    @Test
    void generate_withoutToken_returns401() throws Exception {
        mockMvc.perform(post("/api/briefings/generate"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void generate_withValidToken_returns200() throws Exception {
        when(briefingService.generate()).thenReturn(briefing(BriefingStatus.DONE));

        mockMvc.perform(post("/api/briefings/generate")
                        .header("X-Trigger-Token", "dev-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"));
    }
}
