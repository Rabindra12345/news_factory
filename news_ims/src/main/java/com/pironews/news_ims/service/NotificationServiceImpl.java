package com.pironews.news_ims.service;

import com.pironews.news_ims.model.Notification;
import com.pironews.news_ims.repository.NotificationRepository;
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
