package com.pironews.news_ims.controller;

import com.pironews.news_ims.model.Notification;
import com.pironews.news_ims.service.NotficationServiceI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
public class NotificationController {

    private final NotficationServiceI notificationService;

    public NotificationController(NotficationServiceI notificationService) {
        this.notificationService = notificationService;
    }

    public ResponseEntity<?> getAllNotifications(){
        List<Notification> notifications = notificationService.getNotifications();
        return ResponseEntity.ok(notifications);
    }
}
