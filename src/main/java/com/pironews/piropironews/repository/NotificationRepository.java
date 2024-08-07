package com.pironews.piropironews.repository;

import com.pironews.piropironews.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    public List<Notification> findAll();
}
