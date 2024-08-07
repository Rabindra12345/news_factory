package com.pironews.piropironews.repositories;

import com.pironews.piropironews.entities.NewsPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<NewsPost,String> {

//    LEFT JOIN FETCH a.images i WHERE a.restaurant.id = :restaurantId

    @Query("SELECT n FROM NewsPost n LEFT JOIN n.newsCategory nc WHERE nc.id=:categoryId ")
    public List<NewsPost> getNewsByCategoryId(@Param("categoryId") String categoryId);

    @Query("SELECT n FROM NewsPost n ORDER BY n.publishedDate DESC")
    List<NewsPost> findAllByPublishedDateDesc();
}
