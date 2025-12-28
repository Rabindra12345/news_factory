package com.pironews.recommendationenginehelm.controller;


import com.pironews.recommendationenginehelm.dtos.UserInteractionDTO;
import com.pironews.recommendationenginehelm.models.UserInteraction;
import com.pironews.recommendationenginehelm.service.UserInteractionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/interactions")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class UserInteractionController {

    private final UserInteractionService userInteractionService;

    /**
     * Track user interaction (view, click, etc.)
     * POST /api/interactions/track
     */
    @PostMapping("/track")
    public ResponseEntity<Map<String, Object>> trackInteraction(
            @Valid @RequestBody UserInteractionDTO dto,
            HttpServletRequest request) {

        try {
            UserInteraction interaction = userInteractionService.trackInteraction(dto, request);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Interaction tracked successfully",
                    "interactionId", interaction.getInteractionId().toString()
            ));

        } catch (IllegalArgumentException e) {
            log.warn("Invalid interaction request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));

        } catch (Exception e) {
            log.error("Error tracking interaction", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Failed to track interaction"
            ));
        }
    }

    /**
     * Track read duration (called when user leaves page)
     * POST /api/interactions/read-duration
     */
    @PostMapping("/read-duration")
    public ResponseEntity<Map<String, Object>> trackReadDuration(
            @RequestBody Map<String, Object> payload) {

        try {
            String newsId = (String) payload.get("newsId");
            String sessionId = (String) payload.get("sessionId");
            String userId = (String) payload.get("userId");
            Integer duration = (Integer) payload.get("durationSeconds");

            if (newsId == null || sessionId == null || duration == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Missing required fields"
                ));
            }

            userInteractionService.trackReadDuration(newsId, sessionId, userId, duration);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Read duration tracked successfully"
            ));

        } catch (Exception e) {
            log.error("Error tracking read duration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Failed to track read duration"
            ));
        }
    }

    /**
     * Get user interaction history
     * GET /api/interactions/history?userId=xxx&sessionId=xxx
     */
    @GetMapping("/history")
    public ResponseEntity<List<UserInteraction>> getInteractionHistory(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String sessionId) {

        if (userId == null && sessionId == null) {
            return ResponseEntity.badRequest().build();
        }

        List<UserInteraction> history = userInteractionService
                .getUserInteractionHistory(userId, sessionId);

        return ResponseEntity.ok(history);
    }

    /**
     * Get recent interactions (last N days)
     * GET /api/interactions/recent?userId=xxx&sessionId=xxx&days=7
     */
    @GetMapping("/recent")
    public ResponseEntity<List<UserInteraction>> getRecentInteractions(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String sessionId,
            @RequestParam(defaultValue = "7") int days) {

        if (userId == null && sessionId == null) {
            return ResponseEntity.badRequest().build();
        }

        List<UserInteraction> recent = userInteractionService
                .getRecentInteractions(userId, sessionId, days);

        return ResponseEntity.ok(recent);
    }

    /**
     * Check if user has viewed a specific news
     * GET /api/interactions/has-viewed?newsId=xxx&sessionId=xxx
     */
    @GetMapping("/has-viewed")
    public ResponseEntity<Map<String, Boolean>> hasViewedNews(
            @RequestParam String newsId,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String sessionId) {

        if (sessionId == null && userId == null) {
            return ResponseEntity.badRequest().build();
        }

        boolean hasViewed = userInteractionService
                .hasUserViewedNews(userId, sessionId, newsId);

        return ResponseEntity.ok(Map.of("hasViewed", hasViewed));
    }
}
