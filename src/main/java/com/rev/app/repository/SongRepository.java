package com.rev.app.repository;

import com.rev.app.entity.Song;
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
}
