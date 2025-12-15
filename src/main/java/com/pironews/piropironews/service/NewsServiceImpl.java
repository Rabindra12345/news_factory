package com.pironews.piropironews.service;

import com.pironews.piropironews.dtos.NewsAddDto;
import com.pironews.piropironews.entities.Image;
import com.pironews.piropironews.entities.NewsPost;
import com.pironews.piropironews.model.Category;
import com.pironews.piropironews.repositories.CategoryRepository;
import com.pironews.piropironews.repositories.NewsRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.NotActiveException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class NewsServiceImpl {

    public final static String IMAGE_PATH="/home/ct/Pictures/newsBlog/";

    @Autowired
    private NewsRepository newsRepository;

    private final CategoryRepository categoryRepository;

    List<Category> newsCategoryNames = new ArrayList<>();

    @Autowired
    public NewsServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
        newsCategoryNames= this.categoryRepository.findAll();
    }


    @Value("${time.dateTimeZone}")
    String dateTimeZone;

    @Transactional
    public NewsPost addNews(String title,String textBody,String userId,List<String> newsCategories,List<MultipartFile> images,List<Integer> categoryIds) throws IOException {
        NewsPost newsPost = new NewsPost();
        var filteredNewsCategories = newsCategoryNames.stream().filter(newsCategory -> categoryIds.contains(newsCategory.getId())).collect(Collectors.toList());
        int viewsCount=0;
        String trimmed= "";
        newsPost.setTextTitle(title);
        textBody=textBody.replaceAll("&nbsp;"," ");
        textBody=textBody.replaceAll("<p>"," ");
        textBody=textBody.replaceAll("</p>"," ");
        textBody=textBody.replaceAll("<br>"," ");
        newsPost.setTextBody(textBody);
        newsPost.setPublishedDate(ZonedDateTime.now(ZoneId.of(dateTimeZone)).toLocalDateTime());
        newsPost.setUserId(userId);
        List<Image> imageList = new ArrayList<>();
        newsPost.setNewsId(UUID.randomUUID().toString());
        if(images!=null){
            for(MultipartFile image: images){
                Image imageObj= new Image();
                imageObj.setImageUrl(toString(rawBytes(image)));
                imageObj.setPost(newsPost);
                imageList.add(imageObj);
            }
        }
        newsPost.setImages(imageList);
        if (filteredNewsCategories != null && !filteredNewsCategories.isEmpty()) {
            List<Integer> ids = filteredNewsCategories.stream()
                    .map(Category::getId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();
            List<Category> managedCats = categoryRepository.findAllById(ids);
            newsPost.setNewsCategory(managedCats);
        } else {
            newsPost.setNewsCategory(Collections.emptyList());
        }
        newsPost.setViewsCount(++viewsCount);
        NewsPost savedNews = newsRepository.save(newsPost);
        return newsPost;
    }

    public List<NewsAddDto> fetchAllPopularNews() throws IOException {
        List<NewsPost> newsPosts = newsRepository.findAllPopularNewsPosts();
        return toNewsAddDtos.apply(newsPosts);
    }

    public List<NewsAddDto> fetchAllEntertainmentNews() throws IOException {
        List<NewsPost> newsPosts = newsRepository.findAllEntertainmentNewsPosts();
        return toNewsAddDtos.apply(newsPosts);
    }

    public NewsAddDto fetchNewsWithId(String newsId){
        Optional<NewsPost> newsPost = newsRepository.findById(newsId);
        if (newsPost.isPresent()) {
            CompletableFuture.runAsync(() -> {
                newsRepository.incrementViewCount(newsId);
            });
            return toNewsAddDto.apply(newsPost.get());
        }
        return new NewsAddDto();
    }

    Function<NewsPost,NewsAddDto> toNewsAddDto = newsPost -> {
        NewsAddDto newsAddDto = new NewsAddDto();
        newsAddDto.setPublishedDate(newsPost.getPublishedDate());
        newsAddDto.setTextBody(newsPost.getTextBody());
        newsAddDto.setTextTitle(newsPost.getTextTitle());
        newsAddDto.setImageUrl(newsPost.getImages().stream().map(image ->{
                String b64 = (image.getImageUrl());
                return "data:image/jpeg;base64," + b64;
        }).toList());
        newsAddDto.setUserId(newsPost.getUserId());
        newsAddDto.setNewsId(newsPost.getNewsId());
        return newsAddDto;
    };

    Function<List<NewsPost>,List<NewsAddDto>> toNewsAddDtos = newsPosts ->{
        return newsPosts.stream()
                .map(newsPost -> toNewsAddDto.apply(newsPost))
                .collect(Collectors.toList());
    };

    public List<NewsAddDto> fetchAllNews() throws IOException {
        List<NewsPost> newsPosts = newsRepository.findAllByPublishedDateDesc();
        List<NewsAddDto> newsAddDtoList = new ArrayList<>();
        for (NewsPost newsPost : newsPosts) {
            NewsAddDto newsAddDto = new NewsAddDto();
            newsAddDto.setPublishedDate(newsPost.getPublishedDate());
            newsAddDto.setTextBody(newsPost.getTextBody());
            newsAddDto.setTextTitle(newsPost.getTextTitle());
            newsAddDto.setUserId(newsPost.getUserId());
            newsAddDto.setNewsId(newsPost.getNewsId());
            if (newsPost.getImages() != null) {
                List<String> base64Images = newsPost.getImages().stream()
                        .map(image -> {
                            var imageBytes = (image.getImageUrl());
                            if (imageBytes!=null&& !imageBytes.isBlank()) {
                                    return "data:image/jpeg;base64," + imageBytes;
                                } else {
                                    System.err.println("Failed to read image: " +image.getImageUrl());
                                }
                            return null;
                        })
                        .filter(image -> image != null)
                        .collect(Collectors.toList());
                newsAddDto.setImageUrl(base64Images);
            }
            newsAddDtoList.add(newsAddDto);
        }
        return newsAddDtoList;
    }

    public List<NewsAddDto> fetchNewsByCategoryName(String categoryName) throws NotActiveException {
        if(categoryName==null ||categoryName.isEmpty()){
            throw new NotActiveException("category");
        }
        Optional<Category> getCategory =categoryRepository.getCategoryByName(categoryName);
        if(!getCategory.isPresent()){
            throw new NotActiveException("category");
        }
        List<NewsPost> newsPosts = newsRepository.findAll();

        List<NewsAddDto> newsAddDtoList = new ArrayList<>();

        for (NewsPost newsPost : newsPosts) {
            NewsAddDto newsAddDto = new NewsAddDto();
            newsAddDto.setPublishedDate(newsPost.getPublishedDate());
            newsAddDto.setTextBody(newsPost.getTextBody());
            newsAddDto.setTextTitle(newsPost.getTextTitle());
            newsAddDto.setUserId(newsPost.getUserId());
            if (newsPost.getImages() != null) {
                List<String> base64Images = newsPost.getImages().stream()
                        .map(image -> {
                                var imageBytes = (image.getImageUrl());
                                if (imageBytes!=null&& !imageBytes.isBlank()) {
                                    return "data:image/jpeg;base64," + imageBytes;
                                } else {
                                    System.err.println("Failed to read image: " + image.getImageUrl());
                                }
                            return null;
                        })
                        .filter(image -> image != null)
                        .collect(Collectors.toList());
                newsAddDto.setImageUrl(base64Images);
            }
            newsAddDtoList.add(newsAddDto);
        }
        return newsAddDtoList;

    }

    public static byte[] rawBytes(MultipartFile file) throws IOException {
        return file.getBytes();
    }

    public static String toString(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static void main(String[] args) {
        System.out.println("Customized  local date time _______________:)"+LocalDateTime.now());
        var dateTimeZone = "Europe/Warsaw";
        var sinceDateTime = ZonedDateTime.now(ZoneId.of(dateTimeZone))
                .minusDays(1)
                .toLocalDateTime()
                .withNano(0);
        System.out.println("yesterday  local date time _______________:)"+sinceDateTime);
        System.out.println("Customized  local date time _______________:)"+LocalDateTime.now().atZone(ZoneId.of(dateTimeZone)));
        System.out.println("date time$ _______________:)"+ZonedDateTime.of(LocalDateTime.now(), ZoneId.of(dateTimeZone)).toLocalDateTime());
        System.out.println("date time# _______________:)"+ZonedDateTime.now(ZoneId.of(dateTimeZone)).toLocalDateTime());
    }
}
