package com.pironews.news_ims.controller;

import com.pironews.news_ims.dtos.NewsAddDto;
import com.pironews.news_ims.entities.NewsPost;
import com.pironews.news_ims.model.Category;
import com.pironews.news_ims.service.CategoryServiceImpl;
import com.pironews.news_ims.service.NewsServiceImpl;
import com.pironews.news_ims.utils.Urls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;


@RestController
public class NewsController {

    @Autowired
    private NewsServiceImpl newsService;

    @Autowired
    private CategoryServiceImpl categoryService;

    @PostMapping(value= Urls.CREATE_NEWS,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createNews(
            @RequestParam("title") String title,
            @RequestParam("textBody") String textBody,
            @RequestParam("userId") String userId,
            @RequestParam(value = "newsCategories",required = false) List<String> newsCategories,
            @RequestParam(value = "tags",required = false) List<String> newsTags,
            @RequestParam(value = "textImages",required = false) List<MultipartFile> textImages,@RequestParam("categoryIds") List<Integer> categoryIds) throws IOException {
        NewsPost news=newsService.addNews(title, textBody, userId,newsCategories, textImages, categoryIds,newsTags);
        return ResponseEntity.ok("Form submitted successfully");
    }

    @GetMapping(value=Urls.NEWS_READ,produces = MediaType.APPLICATION_JSON_VALUE)
    public NewsAddDto getNewsByNewsId(@PathVariable String newsId){
        var news=newsService.fetchNewsWithId(newsId);
        return news;
    }

    @GetMapping(value=Urls.NEWS_CATEGORIES,produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Category> getAllCategories() {
        return categoryService.fetchAllCategories();
    }

    @GetMapping(value=Urls.NEWS_READ_ALL,produces = MediaType.APPLICATION_JSON_VALUE)
    public List<NewsAddDto> getAllNews() throws IOException {
        List<NewsAddDto> news=newsService.fetchAllNews();
        return news;
    }

    @GetMapping(value=Urls.ENTERTAINMENT_NEWS_READ,produces = MediaType.APPLICATION_JSON_VALUE)
    public List<NewsAddDto> getAllEntertainmentNews() throws IOException {
        List<NewsAddDto> news=newsService.fetchAllEntertainmentNews();
        return news;
    }

    @GetMapping(value=Urls.POPULAR_NEWS_READ,produces = MediaType.APPLICATION_JSON_VALUE)
    public List<NewsAddDto> getPopularNews() throws IOException {
        List<NewsAddDto> news=newsService.fetchAllPopularNews();
        return news;
    }

    @GetMapping(value=Urls.NEWS_READ_BY_CATEGORY,produces = MediaType.APPLICATION_JSON_VALUE)
    public List<NewsAddDto> getAllNewsByCategories(@PathVariable String categoryName) throws IOException {
        List<NewsAddDto> news=newsService.fetchNewsByCategoryName(categoryName);
        return news;
    }

}
