package com.pironews.piropironews.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@ToString
@NoArgsConstructor
@Data
@Table(name = "event", indexes = {
        @Index(name = "idx_event_type", columnList = "event_type", unique = false),
        @Index(name = "idx_created", columnList = "triggeredDate", unique = false)})
public class Event {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "event_type")
    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @Column(name = "triggered_date")
    private LocalDateTime triggeredDate;

    @JoinColumn(name = "triggered_by")
    @ManyToOne(fetch = FetchType.LAZY)
    private User triggeredBy;

    @OneToMany(mappedBy = "event",cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    private List<Notification> notifications = new ArrayList<>();
}
