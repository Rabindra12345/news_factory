package com.pironews.recommendationenginehelm.repository;

import com.pironews.recommendationenginehelm.models.UserInteraction;
import com.pironews.recommendationenginehelm.models.UserInteraction.InteractionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserInteractionRepository extends JpaRepository<UserInteraction, UUID> {

    @Query("SELECT ui FROM UserInteraction ui WHERE " +
            "(ui.userId = :userId OR ui.sessionId = :sessionId) " +
            "ORDER BY ui.interactionTimestamp DESC")
    List<UserInteraction> findByUserIdOrSessionId(
            @Param("userId") String userId,
            @Param("sessionId") String sessionId
    );

    List<UserInteraction> findByNewsIdOrderByInteractionTimestampDesc(String newsId);

    @Query("SELECT ui FROM UserInteraction ui WHERE " +
            "(ui.userId = :userId OR ui.sessionId = :sessionId) " +
            "AND ui.interactionTimestamp >= :since " +
            "ORDER BY ui.interactionTimestamp DESC")
    List<UserInteraction> findRecentInteractions(
            @Param("userId") String userId,
            @Param("sessionId") String sessionId,
            @Param("since") LocalDateTime since
    );

    @Query("SELECT ui FROM UserInteraction ui WHERE " +
            "(ui.userId = :userId OR ui.sessionId = :sessionId) " +
            "AND ui.interactionType = :type " +
            "ORDER BY ui.interactionTimestamp DESC")
    List<UserInteraction> findByUserAndType(
            @Param("userId") String userId,
            @Param("sessionId") String sessionId,
            @Param("type") InteractionType type
    );

    @Query("SELECT COUNT(ui) > 0 FROM UserInteraction ui WHERE " +
            "(ui.userId = :userId OR ui.sessionId = :sessionId) " +
            "AND ui.newsId = :newsId " +
            "AND ui.interactionType = :type")
    boolean existsByUserAndNewsAndType(
            @Param("userId") String userId,
            @Param("sessionId") String sessionId,
            @Param("newsId") String newsId,
            @Param("type") InteractionType type
    );

    @Query("SELECT DISTINCT ui.newsId FROM UserInteraction ui WHERE " +
            "(ui.userId = :userId OR ui.sessionId = :sessionId) " +
            "AND ui.interactionType IN ('VIEW', 'READ') " +
            "ORDER BY ui.interactionTimestamp DESC")
    List<String> findReadNewsIds(
            @Param("userId") String userId,
            @Param("sessionId") String sessionId
    );

    @Query("SELECT DISTINCT COALESCE(ui.userId, ui.sessionId) FROM UserInteraction ui " +
            "WHERE ui.interactionTimestamp >= :since")
    List<String> findActiveUsersSince(@Param("since") LocalDateTime since);

    @Query("SELECT ui.newsId, COUNT(ui) as interactionCount FROM UserInteraction ui " +
            "WHERE ui.interactionTimestamp >= :since " +
            "GROUP BY ui.newsId " +
            "ORDER BY interactionCount DESC")
    List<Object[]> findPopularNewsSince(@Param("since") LocalDateTime since);
}
