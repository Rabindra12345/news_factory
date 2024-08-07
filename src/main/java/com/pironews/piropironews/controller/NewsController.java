package com.pironews.piropironews.controller;

import com.pironews.piropironews.dtos.NewsAddDto;
import com.pironews.piropironews.entities.NewsPost;
import com.pironews.piropironews.service.NewsServiceImpl;
import com.pironews.piropironews.utils.Urls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
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

    @PostMapping(value= Urls.CREATE_NEWS,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createNews(
            @RequestParam("title") String title,
            @RequestParam("textBody") String textBody,
            @RequestParam("userId") String userId,
            @RequestParam(value = "newsCategories",required = false) List<String> newsCategories,
            @RequestParam(value = "textImages",required = false) List<MultipartFile> textImages) throws IOException {
        NewsPost news=newsService.addNews(title, textBody, userId,newsCategories, textImages);         
        return ResponseEntity.ok("Form submitted successfully");
    }

    @GetMapping(value=Urls.NEWS_READ,produces = MediaType.APPLICATION_JSON_VALUE)
    public NewsPost getNewsByNewsId(@PathVariable String newsId){
        System.out.println("INSDIE NEWS BY ID___________________________________");
        NewsPost news=newsService.fetchNewsWithId(newsId);
        return news;
    }

    @GetMapping(value=Urls.NEWS_READ_ALL,produces = MediaType.APPLICATION_JSON_VALUE)
    public List<NewsAddDto> getAllNews() throws IOException {
        List<NewsAddDto> news=newsService.fetchAllNews();
        return news;
    }

    @GetMapping(value=Urls.NEWS_READ_BY_CATEGORY,produces = MediaType.APPLICATION_JSON_VALUE)
    public List<NewsAddDto> getAllNewsByCategories(@PathVariable String categoryName) throws IOException {
        List<NewsAddDto> news=newsService.fetchNewsByCategoryName(categoryName);
        return news;
    }

}
