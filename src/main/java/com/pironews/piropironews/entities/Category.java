package com.pironews.piropironews.entities;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;

    @ManyToMany(mappedBy="newsCategory")
    @JsonBackReference
    private List<NewsPost> newsPosts;

    public Category(List<NewsPost> newsPosts, String name, Integer id) {
        this.newsPosts = newsPosts;
        this.name = name;
        this.id = id;
    }

    public Category() {
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", newsPosts=" + newsPosts +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<NewsPost> getNewsPosts() {
        return newsPosts;
    }

    public void setNewsPosts(List<NewsPost> newsPosts) {
        this.newsPosts = newsPosts;
    }
}
