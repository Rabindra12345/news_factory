package com.pironews.recommendationenginehelm.models;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_interaction", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_session_id", columnList = "session_id"),
        @Index(name = "idx_news_id", columnList = "news_id"),
        @Index(name = "idx_timestamp", columnList = "interaction_timestamp")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInteraction {

    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(name = "interaction_id", columnDefinition = "UUID")
    private UUID interactionId;

    @Column(name = "user_id")
    private String userId; // Nullable for anonymous users

    @Column(name = "session_id", nullable = false, length = 100)
    private String sessionId;

    @Column(name = "news_id", nullable = false)
    private String newsId;

    @Enumerated(EnumType.STRING)
    @Column(name = "interaction_type", nullable = false, length = 20)
    private InteractionType interactionType;

    @Column(name = "interaction_timestamp", nullable = false)
    private LocalDateTime interactionTimestamp;

    @Column(name = "read_duration_seconds")
    private Integer readDurationSeconds;

    @Column(name = "referrer_url", length = 500)
    private String referrerUrl;

    @Column(name = "device_type", length = 50)
    private String deviceType; // MOBILE, DESKTOP, TABLET

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @PrePersist
    protected void onCreate() {
        if (interactionTimestamp == null) {
            interactionTimestamp = LocalDateTime.now();
        }
    }

    public enum InteractionType {
        VIEW,
        CLICK,
        READ,
        SHARE,
        BOOKMARK,
        COMMENT
    }
}
