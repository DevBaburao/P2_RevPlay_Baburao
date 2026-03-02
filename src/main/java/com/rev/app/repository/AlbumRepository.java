package com.rev.app.repository;

import com.rev.app.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

    List<Album> findByArtistUsername(String username);

    List<Album> findByIsDeleted(Integer isDeleted);

    long countByArtistAndIsDeleted(com.rev.app.entity.User artist, Integer isDeleted);
}
