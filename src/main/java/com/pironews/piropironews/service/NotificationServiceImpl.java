package com.pironews.piropironews.service;

import com.pironews.piropironews.model.Notification;
import com.pironews.piropironews.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author:rabindra
 */
@Service
public class NotificationServiceImpl implements NotficationServiceI{

    private final NotificationRepository repository;

    public NotificationServiceImpl(NotificationRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Notification> getNotifications() {
        return repository.findAll();
    }
}
