package com.pironews.recommendationenginehelm.service;

import com.pironews.recommendationenginehelm.dtos.UserInteractionDTO;
import com.pironews.recommendationenginehelm.models.UserInteraction;
import com.pironews.recommendationenginehelm.models.UserInteraction.InteractionType;
import com.pironews.recommendationenginehelm.repository.NewsRepository;
import com.pironews.recommendationenginehelm.repository.UserInteractionRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserInteractionService {

    private final UserInteractionRepository userInteractionRepository;
    private final NewsRepository newsPostRepository;

    /**
     * Track a user interaction
     */
    @Transactional
    public UserInteraction trackInteraction(UserInteractionDTO dto, HttpServletRequest request) {
        log.debug("Tracking interaction: type={}, newsId={}, sessionId={}",
                dto.getInteractionType(), dto.getNewsId(), dto.getSessionId());

        // Validate news exists
        if (!newsPostRepository.existsById(dto.getNewsId())) {
            throw new IllegalArgumentException("News post not found: " + dto.getNewsId());
        }
        System.out.println("DTO USERID _________________:)"+dto.getUserId());

        UserInteraction interaction = UserInteraction.builder()
                .userId(dto.getUserId())
                .sessionId(dto.getSessionId())
                .newsId(dto.getNewsId())
                .interactionType(dto.getInteractionType())
                .readDurationSeconds(dto.getReadDurationSeconds())
                .referrerUrl(dto.getReferrerUrl())
                .deviceType(dto.getDeviceType())
                .ipAddress(getClientIpAddress(request))
                .interactionTimestamp(LocalDateTime.now())
                .build();

        UserInteraction saved = userInteractionRepository.save(interaction);

        // Increment view count if it's a VIEW interaction
        if (dto.getInteractionType() == InteractionType.VIEW) {
            incrementNewsViewCount(dto.getNewsId());
        }

        return saved;
    }

    /**
     * Track read duration (called when user leaves page)
     */
    @Transactional
    public void trackReadDuration(String newsId, String sessionId, String userId,
                                  Integer durationSeconds) {
        log.debug("Tracking read duration: newsId={}, duration={}s", newsId, durationSeconds);

        UserInteraction interaction = UserInteraction.builder()
                .userId(userId)
                .sessionId(sessionId)
                .newsId(newsId)
                .interactionType(InteractionType.READ)
                .readDurationSeconds(durationSeconds)
                .interactionTimestamp(LocalDateTime.now())
                .build();

        userInteractionRepository.save(interaction);
    }

    /**
     * Get user's interaction history
     */
    @Transactional(readOnly = true)
    public List<UserInteraction> getUserInteractionHistory(String userId, String sessionId) {
        return userInteractionRepository.findByUserIdOrSessionId(userId, sessionId);
    }

    /**
     * Get recent interactions (last 7 days)
     */
    @Transactional(readOnly = true)
    public List<UserInteraction> getRecentInteractions(String userId, String sessionId, int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return userInteractionRepository.findRecentInteractions(userId, sessionId, since);
    }

    /**
     * Check if user has already viewed a news post
     */
    @Transactional(readOnly = true)
    public boolean hasUserViewedNews(String userId, String sessionId, String newsId) {
        return userInteractionRepository.existsByUserAndNewsAndType(
                userId, sessionId, newsId, InteractionType.VIEW
        );
    }

    /**
     * Get user's read news IDs
     */
    @Transactional(readOnly = true)
    public List<String> getUserReadNewsIds(String userId, String sessionId) {
        return userInteractionRepository.findReadNewsIds(userId, sessionId);
    }

    /**
     * Increment news view count (separate method for flexibility)
     */
    private void incrementNewsViewCount(String newsId) {
        newsPostRepository.findById(newsId).ifPresent(news -> {
            news.setViewsCount(news.getViewsCount() == null ? 1 : news.getViewsCount() + 1);
            newsPostRepository.save(news);
        });
    }

    /**
     * Extract client IP address from request
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
