package com.rev.app.repository;

import com.rev.app.entity.Favorite;
import com.rev.app.entity.Song;
import com.rev.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserOrderByCreatedAtDesc(User user);

    Optional<Favorite> findByUserAndSong(User user, Song song);

    void deleteByUserAndSong(User user, Song song);
}
