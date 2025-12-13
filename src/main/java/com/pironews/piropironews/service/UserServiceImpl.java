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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.crypto.*;
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
        String hashedPassword = encryptString(password+salt);
        if (!userFromDb.get().getPassword().equals(hashedPassword)) {
            throw new UsernameNotFoundException("Invalid password for username: " + username);
        }
        String jwt = jwtUtils.generateJwtToken(userFromDb.get());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(username);
        JwtResponse jwtResponse = JwtResponse.builder()
                .accessToken(jwt)
                .roles(userFromDb.get().getRoles().stream().collect(Collectors.toList()))
                .token(refreshToken.getToken())
                .build();
        System.out.println("LOGGING JWT RESPONSE."+jwtResponse);

        return jwtResponse;
    }

    @Transactional
    public User addUser(User user) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        String salt = UUID.randomUUID().toString();
        String hashedPassword = encryptString(user.getPassword()+salt);
        user.setPassword(hashedPassword);
        user.setDateTime(LocalDateTime.now());
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

    public static String encryptString(String originalString) throws InvalidKeySpecException, NoSuchAlgorithmException {
        final MessageDigest digest = MessageDigest.getInstance("SHA3-256");
        final byte[] hashbytes = digest.digest(
                originalString.getBytes(StandardCharsets.UTF_8));
        String sha3Hex = bytesToHex(hashbytes);

        return sha3Hex;
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
