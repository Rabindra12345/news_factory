package com.pironews.piropironews.controller;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.pironews.piropironews.dtos.ApiResponse;
import com.pironews.piropironews.jwt.JwtUtils;
import com.pironews.piropironews.model.ERole;
import com.pironews.piropironews.model.RefreshToken;
import com.pironews.piropironews.model.Role;
import com.pironews.piropironews.model.User;
import com.pironews.piropironews.payload.request.LoginRequest;
import com.pironews.piropironews.payload.request.SignUpRequest;
import com.pironews.piropironews.payload.response.JwtResponse;
import com.pironews.piropironews.payload.response.MessageResponse;
import com.pironews.piropironews.payload.response.RefreshTokenRequestRecord;
import com.pironews.piropironews.repository.RefreshTokenRepository;
import com.pironews.piropironews.repository.RoleRepository;
import com.pironews.piropironews.repository.UserRepository;
import com.pironews.piropironews.service.RefreshTokenService;
import com.pironews.piropironews.service.UserDetailsServiceImpl;
import com.pironews.piropironews.service.UserServiceImpl;
//import io.swagger.v3.oas.models.responses.ApiResponse;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final Logger logger = LogManager.getLogger(AuthController.class);
    @Autowired
    AuthenticationManager authenticationManager;

//    @Autowired
//    RefreshTokenService refreshTokenService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    RoleRepository roleRepository;


    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<?>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, InvalidKeySpecException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        JwtResponse jwtResponse = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
            return ResponseEntity.ok(ApiResponse.getBody(jwtResponse));
    }


    @PostMapping(value = "/refresh",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAccessTokenUsingRefreshToken(@RequestBody RefreshTokenRequestRecord refreshTokenRequestRecord){
        String refreshToken = refreshTokenRequestRecord.token();
        if(refreshToken == null){
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid refresh token"));
        }
        RefreshToken refreshTokenInstance = refreshTokenService.findByToken(refreshToken).get();
        var mayBeUser = this.userRepository.findById(refreshTokenInstance.getUserInfo().getId());
        this.refreshTokenRepository.deleteLinksByUserId(mayBeUser.get().getId());
        RefreshToken toBeSavedRefreshToken = RefreshToken.builder()
                .userInfo(userRepository.findByUsername(mayBeUser.get().getUsername()).get())
                .token(refreshTokenRequestRecord.token())
                .expiryDate(LocalDateTime.now().plusMinutes(2))
                .build();
        this.refreshTokenRepository.save(toBeSavedRefreshToken);
        if(refreshTokenService.verifyExpiration(toBeSavedRefreshToken)){
            String newAccessToken = jwtUtils.generateJwtTokenWithUserInfo(refreshTokenInstance.getUserInfo());
            return ResponseEntity.ok(JwtResponse.builder()
                    .accessToken(newAccessToken)
                    .token(refreshTokenRequestRecord.token())
                    .build());
        }
        return ResponseEntity.ok("token verification failed.");


    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) throws InvalidAlgorithmParameterException, NoSuchPaddingException, InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.findByUsername(signUpRequest.getUsername()).isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                signUpRequest.getPassword());

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role role = new Role();
            role.setName(ERole.ROLE_USER);
            roles.add(role);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Optional<Role> adminRole = roleRepository.findByName(ERole.ROLE_ADMIN);
                        Role roleObj = new Role();
                        roleObj.setName(ERole.ROLE_ADMIN);
                        roles.add(adminRole.orElse(roleObj));

                        break;
                    case "mod":
                        Optional<Role> modRole = roleRepository.findByName(ERole.ROLE_MODERATOR);
                        Role roleObj1 = new Role();
                        roleObj1.setName(ERole.ROLE_MODERATOR);
                        roles.add(modRole.orElse(roleObj1));

                        break;
                    default:
                        Optional<Role> userRole = roleRepository.findByName(ERole.ROLE_USER);
                        Role roleObj2 = new Role();
                        roleObj2.setName(ERole.ROLE_USER);
                        roles.add(userRole.orElse(roleObj2));
                }
            });
        }
        System.out.println("logging roles :"+roles);
        user.setRoles(roles);
        User savedUser =userService.addUser(user);

        return ResponseEntity.ok(savedUser);
    }


    @GetMapping("/current-user")
    public ResponseEntity<UserDetails> getCurrentLoggedInUserDetails(Principal principal){
        logger.info("PRINTING CURRENTLY LOGGED IN USER__time changed.");
        UserDetails user =  userDetailsService.loadUserByUsername(principal.getName());
        return ResponseEntity.ok(user);
    }
}
