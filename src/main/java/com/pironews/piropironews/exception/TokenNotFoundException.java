package com.baldur.jwtauth.exception;

public class TokenNotFoundException extends RuntimeException{

    private int statusCode;

    private String message;

    private String errorDescription;

    public TokenNotFoundException(int statusCode, String errorDescription,String message) {
        this.statusCode = statusCode;
        this.errorDescription = errorDescription;
        this.message = message;
    }
}
