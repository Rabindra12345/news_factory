package com.pironews.piropironews.service;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class NewsServiceImplTests {


    @InjectMocks
    private NewsServiceImpl newsService;


    @Test
    @DisplayName("pruning leading and trailing tag and spaces")
    public void testTrimLeadingAndTrailingSpaces(){
        String str="<p>kina kina, man a timi lai&nbsp;<br>bugheu parkha timilai&nbsp;<br>bugheu ?<br>bugheu ki nai ta ?</p></p>";
        String trimmed= "";
        str=str.replaceAll("&nbsp;"," ");
        str=str.replaceAll("<p>"," ");
        str=str.replaceAll("</p>"," ");
        str=str.replaceAll("<br>"," ");

        System.out.println("printing __ "+str);
        System.out.println("the final string :"+trimmed+":");
    }

}
