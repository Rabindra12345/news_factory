//package com.baldur.jwtauth.controller;
//
//import com.baldur.jwtauth.model.User;
//import com.baldur.jwtauth.repository.UserRepository;
//import com.baldur.jwtauth.service.UserDetailsImpl;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.web.bind.annotation.*;
//import java.util.Optional;
////import java.util.logging.LogManager;
////import java.util.logging.Logger;
//
//@CrossOrigin(origins = "*", maxAge = 3600)
//@RestController
//@RequestMapping("/api/test")
//public class DemoController {
//
//    private final Logger logger = LogManager.getLogger(DemoController.class);
//
//    @Autowired
//    AuthenticationManager authenticationManager;
//
//    @Autowired
//    private UserRepository userRepository;
//    @GetMapping("/all")
//    public String allAccess() {
//        return "Public Content.";
//    }
//
//    @GetMapping("/user")
//    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
//    public String userAccess() {
//
//        return "User Content.";
//    }
//
//    @GetMapping("/mod")
//    @PreAuthorize("hasRole('MODERATOR')")
//    public String moderatorAccess() {
//        return "Moderator Board.";
//    }
//
//    @GetMapping("/admin")
//    @PreAuthorize("hasRole('ADMIN')")
//    public String adminAccess() {
//        return "Admin Board.";
//    }
//
//    @GetMapping("/{username}")
//    public ResponseEntity<UserDetails> getUserDetails(@PathVariable("username") String username) {
////
////        Optional<User> userFromDb = userRepository.findByUsername(username);
////        Authentication authentication = authenticationManager.authenticate(
////                new UsernamePasswordAuthenticationToken(userFromDb.get().getUsername(), userFromDb.get().getPassword()));
////
////        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetails) {
////            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
////            return ResponseEntity.ok(userDetails);
////        } else {
////            // Handle the case where user is not authenticated
////            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
////        }
//
//        logger.trace("This is a TRACE message");
//        logger.debug("This is a DEBUG message");
//        logger.info("This is an INFO message");
//        logger.warn("This is a WARN message");
//        logger.error("This is an ERROR message");
//        Optional<User> user = userRepository.findByUsername(username);
//        if (user.isPresent()) {
//            return ResponseEntity.ok(UserDetailsImpl.build(user.get()));
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
//
//    }
//}
