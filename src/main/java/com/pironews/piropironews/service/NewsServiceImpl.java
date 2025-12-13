package com.pironews.piropironews.service;

import com.pironews.piropironews.dtos.NewsAddDto;
import com.pironews.piropironews.entities.Image;
import com.pironews.piropironews.entities.NewsPost;
import com.pironews.piropironews.model.Category;
import com.pironews.piropironews.repositories.CategoryRepository;
import com.pironews.piropironews.repositories.NewsRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class NewsServiceImpl {

    public final static String IMAGE_PATH="/home/ct/Pictures/newsBlog/";

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional
    public NewsPost addNews(String title,String textBody,String userId,List<String> newsCategories,List<MultipartFile> images,Integer category) throws IOException {
        NewsPost newsPost = new NewsPost();
        var mayBeCategory = categoryRepository.findById(category);
        int viewsCount=0;
        String trimmed= "";
        newsPost.setTextTitle(title);
        textBody=textBody.replaceAll("&nbsp;"," ");
        textBody=textBody.replaceAll("<p>"," ");
        textBody=textBody.replaceAll("</p>"," ");
        textBody=textBody.replaceAll("<br>"," ");
        newsPost.setTextBody(textBody);
        newsPost.setPublishedDate(LocalDateTime.now());
        newsPost.setUserId(userId);
        List<Image> imageList = new ArrayList<>();
        newsPost.setNewsId(UUID.randomUUID().toString());
        if(images!=null){
            for(MultipartFile image: images){
//                var news = new NewsPost();
//                news.setNewsId(String.valueOf(newsId+1));
                Image imageObj= new Image();
                imageObj.setImageUrl(writeImage(image));
                imageObj.setPost(newsPost);
                imageList.add(imageObj);
//                writeImage(image);
            }
        }
        newsPost.setImages(imageList);
        var filteredCatList = new ArrayList<Category>();
        if(newsCategories!=null){
            for(String newsCategory: newsCategories){
                Optional<Category> getCategory =categoryRepository.getCategoryByName(newsCategory);
                if(getCategory.isEmpty()){
                    Category cat = new Category();
                    cat.setName(newsCategory);
                    filteredCatList.add(cat);
                }
            }
        }
//        category.setName(newsCategory);

        if(filteredCatList!=null&&filteredCatList.size()>0){
            newsPost.setNewsCategory(filteredCatList);
        }
        newsPost.setViewsCount(++viewsCount);
        newsPost.setCategory(mayBeCategory.get().getName());
        NewsPost savedNews = newsRepository.save(newsPost);
        return newsPost;
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
            try {
                String b64 = convertToBase64(readImageBytes(image.getImageUrl()));
                return "data:image/jpeg;base64," + b64;
            } catch (IOException e) {
                throw new RuntimeException("Failed to read image: " + image.getImageUrl(), e);
            }
        }).toList());
        newsAddDto.setUserId(newsPost.getUserId());
        newsAddDto.setNewsId(newsPost.getNewsId());
        return newsAddDto;
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
                            try {
                                byte[] imageBytes = readImageBytes(image.getImageUrl());
                                if (imageBytes.length > 0) {
                                    return "data:image/jpeg;base64," + convertToBase64(imageBytes);
                                } else {
                                    System.err.println("Failed to read image: " + image.getImageUrl());
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return null;
                        })
                        .filter(image -> image != null)
                        .collect(Collectors.toList());

//                newsAddDto.setImageUrls(base64Images);
                newsAddDto.setImageUrl(base64Images);
            }

            newsAddDtoList.add(newsAddDto);
        }
        return newsAddDtoList;
    }


    public String convertToBase64(byte[] imageBytes) {
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    public static byte[] readImageBytes(String imagePath) throws IOException {
        Path path = Paths.get(imagePath);
        if (!Files.exists(path)) {
            System.err.println("File not found: " + imagePath);
            return new byte[0];
        }
        try {
            System.out.println("Reading image: " + Files.readAllBytes(path).toString());
            return Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }


    public String writeImage(MultipartFile image) throws IOException {
        if (image.getSize() != 0) {
            String imagePath = IMAGE_PATH + image.getOriginalFilename();
            System.out.println("Writing image__________________________ : " +imagePath);
            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, Paths.get(imagePath), StandardCopyOption.REPLACE_EXISTING);
            }
            return imagePath;
        }
        return "";
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
                            try {
                                byte[] imageBytes = readImageBytes(image.getImageUrl());
                                if (imageBytes.length > 0) {
                                    return "data:image/jpeg;base64," + convertToBase64(imageBytes);
                                } else {
                                    System.err.println("Failed to read image: " + image.getImageUrl());
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return null;
                        })
                        .filter(image -> image != null)
                        .collect(Collectors.toList());

//                newsAddDto.setImageUrls(base64Images);
                newsAddDto.setImageUrl(base64Images);
            }

            newsAddDtoList.add(newsAddDto);
        }
        return newsAddDtoList;

    }
}
