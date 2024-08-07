package com.pironews.piropironews.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SingleSessionLoginService
{

    private final ConcurrentHashMap<String, String> activeTokens = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> userTokens = new ConcurrentHashMap<>();

    public boolean isTokenAlreadyUsed(String token) {
        return activeTokens.containsKey(token);
    }

    public void addToken(String token, String username) {
        if (userTokens.containsKey(username)) {
            String oldToken = userTokens.get(username);
            activeTokens.remove(oldToken);
        }
        activeTokens.put(token, username);
        userTokens.put(username, token);
    }

    public void removeToken(String token) {
        String username = activeTokens.remove(token);
        if (username != null) {
            userTokens.remove(username);
        }
    }
}
