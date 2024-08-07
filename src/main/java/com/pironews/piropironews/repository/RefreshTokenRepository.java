package com.pironews.piropironews.repository;

import com.pironews.piropironews.model.RefreshToken;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByToken(String token);

//    @Query("SELECT * FROM RefreshToken limit 1")
//    Optional<RefreshToken> findByRefreshToken(int limit );
}
