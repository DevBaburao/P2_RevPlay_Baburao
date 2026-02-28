package com.rev.app.repository;

import com.rev.app.entity.Playlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    Page<Playlist> findByIsDeleted(Integer isDeleted, Pageable pageable);

    List<Playlist> findByNameContainingIgnoreCaseAndIsDeleted(String name, Integer isDeleted);
}
