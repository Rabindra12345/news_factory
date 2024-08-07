package com.pironews.piropironews.service;

import com.pironews.piropironews.jwt.JwtUtils;
import com.pironews.piropironews.model.RefreshToken;
import com.pironews.piropironews.model.Role;
import com.pironews.piropironews.model.User;
import com.pironews.piropironews.payload.response.JwtResponse;
import com.pironews.piropironews.repository.RoleRepository;
import com.pironews.piropironews.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl {

//    @Value("${baldur.app.jwtSecret}")
    private static String jwtSecret="======================rabindra=Spring===========================";

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RoleRepository roleRepository;


    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    UserRepository userRepository;

    @Transactional
    public JwtResponse login(String username, String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        Optional<User> userFromDb = userRepository.findByUsername(username);
        if (userFromDb.isEmpty()) {
            throw new UsernameNotFoundException("User Not Found with username: " + username);
        }

        String salt = userFromDb.get().getSalt();
        if (!verifyPassword(password, salt, userFromDb.get().getPassword())) {
            throw new UsernameNotFoundException("Invalid username or password");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateJwtToken(authentication);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(username);

        JwtResponse jwtResponse = JwtResponse.builder()
                .accessToken(jwt)
                .roles(userFromDb.get().getRoles().stream().collect(Collectors.toList()))
                .token(refreshToken.getToken())
                .build();

        return jwtResponse;
    }

    @Transactional
    public User addUser(User user) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        String salt = generateSalt();
        String hashedPassword = hashPassword(user.getPassword(), salt);
        user.setPassword(hashedPassword);
        user.setSalt(salt);

        Set<Role> rolesToSet = new HashSet<>();
        for (Role role : user.getRoles()) {
            Role existingRole = roleRepository.findByName(role.getName())
                    .orElseGet(() -> roleRepository.save(new Role(role.getName())));
            rolesToSet.add(existingRole);
        }

        user.setRoles(rolesToSet);
        userRepository.save(user);
        return user;
    }

//    public static String encryptString(String password,String salt) throws InvalidKeySpecException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
//        byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
//        IvParameterSpec ivspec = new IvParameterSpec(iv);
//        /* Create factory for secret keys. */
//        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
//        /* PBEKeySpec class implements KeySpec interface. */
//        KeySpec spec = new PBEKeySpec(jwtSecret.toCharArray(), salt.getBytes(), 65536, 256);
//        SecretKey tmp = factory.generateSecret(spec);
//        SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
//        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
//        /* Retruns encrypted value. */
//        return Base64.getEncoder()
//                .encodeToString(cipher.doFinal(password.getBytes(StandardCharsets.UTF_8)));
//    }
//
//    public static String decryptString(String password) throws InvalidKeySpecException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
//        byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
//        IvParameterSpec ivspec = new IvParameterSpec(iv);
//        /* Create factory for secret keys. */
//        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
//        /* PBEKeySpec class implements KeySpec interface. */
//        KeySpec spec = new PBEKeySpec(jwtSecret.toCharArray(), password.getBytes(), 65536, 256);
//        SecretKey tmp = factory.generateSecret(spec);
//        SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
//        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
//        /* Retruns encrypted value. */
//        return Base64.getEncoder()
//                .encodeToString(cipher.doFinal(password.getBytes(StandardCharsets.UTF_8)));
//    }
public static String hashPassword(String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
    byte[] saltBytes = salt.getBytes(StandardCharsets.UTF_8);

    KeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, 65536, 256);
    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    byte[] hash = factory.generateSecret(spec).getEncoded();

    return Base64.getEncoder().encodeToString(hash);
}

    public static boolean verifyPassword(String password, String salt, String hashedPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String hashedAttempt = hashPassword(password, salt);
        return hashedAttempt.equals(hashedPassword);
    }

    public static String generateSalt() throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] salt = md.digest(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(salt);
    }
}
