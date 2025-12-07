package com.pironews.piropironews.repository;

import com.pironews.piropironews.model.RefreshToken;
import com.pironews.piropironews.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUserInfo(User user);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM refresh_token WHERE user_id = :userId", nativeQuery = true)
    void deleteLinksByUserId(@Param("userId") Long userId);

//    @Query("SELECT * FROM RefreshToken limit 1")
//    Optional<RefreshToken> findByRefreshToken(int limit );
}
