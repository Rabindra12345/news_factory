package com.pironews.piropironews.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@ToString
@Table(name = "notification", indexes = {
        @Index(name = "idx_read", columnList = "`read`", unique = false),
        @Index(name = "idx_created", columnList = "created", unique = false),})
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "`read`")
    private boolean read;

    @Column(name = "created")
    private LocalDateTime created;

    @Column(name = "message")
    private String message;

    @Column(name = "link")
    private String link;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @JoinColumn(name = "sender_user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User sender;

}
