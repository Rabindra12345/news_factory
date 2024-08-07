package com.pironews.piropironews.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NewsAddDto {

    private String newsId;

    private String userId;

    private String textBody;

    private List<String> imageUrl;

    private String textTitle;

    private LocalDateTime publishedDate;

    public NewsAddDto() {
    }

    public NewsAddDto(String newsId, String userId, String textBody, List<String> imageUrl,String textTitle, LocalDateTime publishedDate) {
        this.newsId = newsId;
        this.userId = userId;
        this.textBody = textBody;
        this.imageUrl = imageUrl;
        this.textTitle = textTitle;
        this.publishedDate = publishedDate;
    }

    @Override
    public String toString() {
        return "NewsAddDto{" +
                "newsId='" + newsId + '\'' +
                ", userId='" + userId + '\'' +
                ", textBody='" + textBody + '\'' +
                ", imageUrl=" + imageUrl +
                ", textTitle='" + textTitle + '\'' +
                ", publishedDate=" + publishedDate +
                '}';
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTextBody() {
        return textBody;
    }

    public void setTextBody(String textBody) {
        this.textBody = textBody;
    }

    public List<String> getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(List<String> imageUrl) {
        this.imageUrl = imageUrl;
//        for (byte[] image : imageUrl) {
//        this.imageUrl = imageUrl;
    }

    public String getTextTitle() {
        return textTitle;
    }

    public void setTextTitle(String textTitle) {
        this.textTitle = textTitle;
    }

    public LocalDateTime getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(LocalDateTime publishedDate) {
        this.publishedDate = publishedDate;
    }
}
