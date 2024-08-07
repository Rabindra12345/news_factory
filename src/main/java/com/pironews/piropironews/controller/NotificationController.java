package com.pironews.piropironews.controller;

import com.pironews.piropironews.model.Notification;
import com.pironews.piropironews.service.NotficationServiceI;
import org.springframework.http.HttpStatus;
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
