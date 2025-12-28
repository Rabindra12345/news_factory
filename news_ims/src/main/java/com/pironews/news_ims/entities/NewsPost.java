package com.pironews.news_ims.entities;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.pironews.news_ims.model.Category;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="news_post")
public class NewsPost {

    @Id
    @Column(name="news_id")
    private String newsId;

    private String userId;

    @Column(name = "text_body", columnDefinition = "VARCHAR(10000)")
    private String textBody;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "post")
    private List<Image> images;


    private String textTitle;

    private LocalDateTime publishedDate;

    private String category;

    private Integer viewsCount;

    @ManyToMany
    @JoinTable(
            name = "news_category",
            joinColumns = @JoinColumn(name = "news_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @JsonManagedReference
    private List<Category> newsCategory;

    @ElementCollection
    @CollectionTable(
            name = "news_post_tags",
            joinColumns = @JoinColumn(name = "news_id", referencedColumnName = "news_id")
    )
    @Column(name = "tag")
    private Set<String> tags = new LinkedHashSet<>();


    public NewsPost() {
    }

    public NewsPost(LocalDateTime publishedDate, String textTitle, String textBody, String userId, String newsId,String category,Integer viewsCount,Set<String>tags) {
        this.publishedDate = publishedDate;
        this.textTitle = textTitle;
//        this.imageUrl = imageUrl;
        this.textBody = textBody;
        this.userId = userId;
        this.newsId = newsId;
        this.category = category;
        this.viewsCount = viewsCount;
        this.tags=tags;
    }

    @Override
    public String toString() {
        return "NewsPost{" +
                "newsId='" + newsId + '\'' +
                ", userId='" + userId + '\'' +
                ", textBody='" + textBody + '\'' +
                ", textTitle='" + textTitle + '\'' +
                ", publishedDate=" + publishedDate +
                ", newsCategory=" + newsCategory +
                '}';
    }


    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
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

    public List<Category> getNewsCategory() {
        return newsCategory;
    }

    public void setNewsCategory(List<Category> newsCategory) {
        this.newsCategory = newsCategory;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(Integer viewsCount) {
        this.viewsCount = viewsCount;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }
}
