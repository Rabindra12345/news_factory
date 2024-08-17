package com.pironews.piropironews.dtos;

//@Setter
public class ApiResponse<T> {

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    private T body;

    public static <T> ApiResponse<?> getBody( T body) {
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.setBody(body);
        return apiResponse;

    }


}
