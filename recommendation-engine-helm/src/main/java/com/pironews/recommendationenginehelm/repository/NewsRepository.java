package com.pironews.recommendationenginehelm.repository;

import com.pironews.recommendationenginehelm.models.NewsPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends JpaRepository<NewsPost,String> {

}
