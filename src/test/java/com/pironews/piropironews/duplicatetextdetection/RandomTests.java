package com.pironews.piropironews.duplicatetextdetection;


//import org.apache.lucene.search.spell.LevenshteinDistance;
import org.apache.commons.text.similarity.LevenshteinDistance;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RandomTests {

    private static final String SECRET_KEY = "123456789";
    private static final String SALTVALUE = "abcdefg";

    @Test
    @Disabled
    @DisplayName("comparing two texts for euality")
    public void compareTextsEquality_success_condition(){
        String text1 ="Vice President Kamala Harris will be absent from the rostrum.\n" +
                "\n" +
                "The rows of seats on the Democratic side of the House chamber will be conspicuously emptier than those on the Republican side.\n" +
                "\n" +
                "And tensions are running so high that Speaker Mike Johnson has threatened to have anyone who causes a disturbance on the floor or in the gallery above arrested.\n" +
                "\n" +
                "When Prime Minister Benjamin Netanyahu of Israel arrives on Capitol Hill on Wednesday afternoon to address a joint meeting of Congress, he will confront a legislative body divided over his leadership in the face of international censure over the war in Gaza, with some showing open hostility to the government of a country that is supposed to be among the United States’ closest allies.\n" +
                "\n" +
                "“I will seek to anchor the bipartisan support that is so important for Israel,” Mr. Netanyahu said before departing Israel for his visit to Washington.";
        String text2 ="Vice President Kamala Harris will be absent from the rostrum.\n" +
                "\n" +
                "The rows of seats on the Democratic side of the House chamber will be conspicuously emptier than those on the Republican side.\n" +
                "\n" +
                "And tensions are running so high that Speaker Mike Johnson has threatened to have anyone who causes a disturbance on the floor or in the gallery above arrested.\n" +
                "\n" +
                "When Prime Minister Benjamin Netanyahu of Israel arrives on Capitol Hill on Wednesday afternoon to address a joint meeting of Congress, he will confront a legislative body divided over his leadership in the face of international censure over the war in Gaza, with some showing open hostility to the government of a country that is supposed to be among the United States’ closest allies.\n" +
                "\n" +
                "“I will seek to anchor the bipartisan support that is so important for Israel,” Mr. Netanyahu said before departing Israel for his visit to Washington.";

        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
        int val = levenshteinDistance.apply(text1, text2);
        assertEquals(0, val);
        System.out.println("value is "+val);
    }

    @Test
    @Disabled
    @DisplayName("checking if two texts are mostly equal")
    public void checkIfTwoTextsAreMostlyEqual_success_conditions() {
        String text1 ="Vice President Kamala Harris will be absent from the rostrum.\n" +
                "\n" +
                "The rows of seats on the Democratic side of the House chamber will be conspicuously emptier than those on the Republican side.\n" +
                "\n" +
                "And tensions are running so high that Speaker Mike Johnson has threatened to have anyone who causes a disturbance on the floor or in the gallery above arrested.\n" +
                "\n" +
                "When Prime Minister Benjamin Netanyahu of Israel arrives on Capitol Hill on Wednesday afternoon to address a joint meeting of Congress, he will confront a legislative body divided over his leadership in the face of international censure over the war in Gaza, with some showing open hostility to the government of a country that is supposed to be among the United States’ closest allies.\n" +
                "\n" +
                "“I will seek to anchor the bipartisan support that is so important for Israel you know what?,” Mr. Netanyahu said before departing Israel for his visit to Washington.";
        String text2 ="Vice President Kamala Harris will be absent from the rostrum.\n" +
                "\n" +
                "The rows of seats on the Democratic side of the House chamber will be conspicuously emptier than those on the Republican side.\n" +
                "\n" +
                "And tensions are running so high that Speaker Mike Johnson has threatened to have anyone who causes a disturbance on the floor or in the gallery above arrested.\n" +
                "\n" +
                "When Prime Minister Benjamin Netanyahu of Israel arrives on Capitol Hill on Wednesday afternoon to address a joint meeting of Congress, he will confront a legislative body divided over his leadership in the face of international censure over the war in Gaza, with some showing open hostility to the government of a country that is supposed to be among the United States’ closest allies.\n" +
                "\n" +
                "“I will seek to anchor the bipartisan support that is so important for Israel,” Mr. Netanyahu said before departing Israel for his visit to Washington.";
        LevenshteinDistance distance = new LevenshteinDistance();
        int val = distance.apply(text1, text2);
        System.out.println("value is "+val);
        boolean found = false;
        if(val<=15) {
            found = true;
            assertTrue(found);

        }else{
//            found = false;
            assertTrue(found);
        }

    }

    @Test
    public void testEncryptionDecryption() throws Exception {
//        String input = "Hello World";
        String name ="Rabindra";
        String encrypted = encrypt(name);
        System.out.println("Input _____________________ : " + name);
        System.out.println("Encrypted: " + encrypted);
        String decrypted = decrypt(encrypted);
        System.out.println("Decrypted_______________________ : " + decrypted);
        assertEquals(name, decrypted);
        System.out.println("Encryption and Decryption test passed");
    }

    public static String encrypt(String strToEncrypt)
    {
        try
        {
            /* Declare a byte array. */
            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            /* Create factory for secret keys. */
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            /* PBEKeySpec class implements KeySpec interface. */
            KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALTVALUE.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
            /* Retruns encrypted value. */
            return Base64.getEncoder()
                    .encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        }
        catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e)
        {
            System.out.println("Error occured during encryption: " + e.toString());
        }
        return null;
    }

    /* Decryption Method */
    public static String decrypt(String strToDecrypt)
    {
        try
        {
            /* Declare a byte array. */
            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            /* Create factory for secret keys. */
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            /* PBEKeySpec class implements KeySpec interface. */
            KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALTVALUE.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
            /* Retruns decrypted value. */
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        }
        catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException |
               InvalidKeySpecException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e)
        {
            System.out.println("Error occured during decryption: " + e.toString());
        }
        return null;
    }



}
