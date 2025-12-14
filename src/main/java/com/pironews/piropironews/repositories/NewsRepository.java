package com.pironews.piropironews.repositories;

import com.pironews.piropironews.entities.NewsPost;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Query(value = """
    SELECT np.*
    FROM news_post np
    WHERE np.published_date >= NOW() - INTERVAL '1 day'
    ORDER BY (
        LOG10(1 + np.views_count) /
        (1 + (EXTRACT(EPOCH FROM (NOW() - np.published_date)) / 3600) / 24)
    ) DESC
    LIMIT 5
    """, nativeQuery = true)
    List<NewsPost> findAllPopularNewsPosts();


    @Query(value = """
        WITH entertained_posts AS (
            SELECT DISTINCT nc.news_id
            FROM news_category nc
                JOIN category c ON c.id = nc.category_id
            WHERE c.name = 'Entertainment'
        )
    SELECT np.*
    FROM news_post np
    JOIN entertained_posts ep ON ep.news_id = np.news_id
    order by np.published_date desc LIMIT 5
    """, nativeQuery = true)
    List<NewsPost> findAllEntertainmentNewsPosts();

    @Modifying
    @Transactional
    @Query("UPDATE NewsPost n SET n.viewsCount = n.viewsCount + 1 WHERE n.newsId=:newsId")
    void incrementViewCount(@Param("newsId") String newsId);
}
