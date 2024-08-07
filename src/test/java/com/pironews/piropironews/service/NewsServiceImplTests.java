package com.pironews.piropironews.service;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class NewsServiceImplTests {


    @InjectMocks
    private NewsServiceImpl newsService;

    @Test
    @DisplayName("reading from image success test case.")
    public void testReadFromPathAndConvertToBytes() throws IOException {
        String path = "/home/rabindra-jar/Pictures/rabindra.png";
        byte[] bytes = newsService.readImageBytes(path);

        System.out.println("Read from image __ "+ Arrays.toString(bytes));

    }

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
