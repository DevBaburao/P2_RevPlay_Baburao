package com.rev.app.repository;

import com.rev.app.entity.Song;
import com.rev.app.entity.ArtistProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findByIsDeleted(Integer isDeleted);

    Page<Song> findByIsDeleted(Integer isDeleted, Pageable pageable);

    List<Song> findByTitleContainingIgnoreCaseAndIsDeleted(String title, Integer isDeleted);

    Long countByArtistAndIsDeleted(ArtistProfile artist, Integer isDeleted);

    @org.springframework.data.jpa.repository.Query("SELECT s FROM Song s WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(s.artist.artistName) LIKE LOWER(CONCAT('%', :artist, '%'))")
    List<Song> findByNameContainingIgnoreCaseOrArtistContainingIgnoreCase(
            @org.springframework.data.repository.query.Param("name") String name,
            @org.springframework.data.repository.query.Param("artist") String artist);

    @org.springframework.data.jpa.repository.Query("SELECT s FROM Song s WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(s.artist.artistName) LIKE LOWER(CONCAT('%', :artist, '%'))")
    org.springframework.data.domain.Page<Song> findByNameContainingIgnoreCaseOrArtistContainingIgnoreCase(
            @org.springframework.data.repository.query.Param("name") String name,
            @org.springframework.data.repository.query.Param("artist") String artist,
            org.springframework.data.domain.Pageable pageable);
}
