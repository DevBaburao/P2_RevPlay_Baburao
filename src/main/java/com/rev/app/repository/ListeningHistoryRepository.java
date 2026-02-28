package com.rev.app.repository;

import com.rev.app.entity.ListeningHistory;
import com.rev.app.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import com.rev.app.entity.ArtistProfile;

import java.util.List;

@Repository
public interface ListeningHistoryRepository extends JpaRepository<ListeningHistory, Long> {
    List<ListeningHistory> findTop50ByUserOrderByPlayedAtDesc(User user);

    @Query("SELECT h.song, COUNT(h) FROM ListeningHistory h WHERE h.song.isDeleted = 0 GROUP BY h.song ORDER BY COUNT(h) DESC")
    List<Object[]> findTopPlayedSongs(Pageable pageable);

    @Query("SELECT h.song, COUNT(h) FROM ListeningHistory h WHERE h.song.isDeleted = 0 AND h.playedAt >= :sevenDaysAgo GROUP BY h.song ORDER BY COUNT(h) DESC")
    List<Object[]> findTrendingSongs(@Param("sevenDaysAgo") Timestamp sevenDaysAgo, Pageable pageable);

    @Query("SELECT COUNT(h) FROM ListeningHistory h WHERE h.song.artist = :artist AND h.song.isDeleted = 0")
    Long countTotalPlaysByArtist(@Param("artist") ArtistProfile artist);

    @Query("SELECT h.song, COUNT(h) FROM ListeningHistory h WHERE h.song.artist = :artist AND h.song.isDeleted = 0 GROUP BY h.song ORDER BY COUNT(h) DESC")
    List<Object[]> findMostPlayedSongByArtist(@Param("artist") ArtistProfile artist, Pageable pageable);
}
