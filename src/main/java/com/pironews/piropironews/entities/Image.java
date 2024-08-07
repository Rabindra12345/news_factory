package com.pironews.piropironews.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name="image")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name="news_post_id")
    @JsonBackReference
    private NewsPost post;

    public Image() {
    }

    public Image(int id, String imageUrl, NewsPost post) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.post = post;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public NewsPost getPost() {
        return post;
    }

    public void setPost(NewsPost post) {
        this.post = post;
    }
}
