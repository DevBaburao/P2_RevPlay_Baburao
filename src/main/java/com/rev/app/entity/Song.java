package com.rev.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Table(name = "songs")
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "artist_id", nullable = false)
    private User artist;

    @ManyToOne
    @JoinColumn(name = "album_id", nullable = true)
    private Album album;

    @ManyToOne
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;

    @Column(nullable = false, length = 200)
    private String title;

    private Long duration;

    @Column(name = "audio_url", length = 500)
    private String audioUrl;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "release_date")
    private Date releaseDate;

    @Column(length = 20)
    private String visibility;

    @Column(length = 20)
    private String status;

    @Column(name = "play_count", columnDefinition = "NUMBER DEFAULT 0")
    private Long playCount = 0L;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "is_deleted")
    private Integer isDeleted = 0;

    @jakarta.persistence.ManyToMany(mappedBy = "likedSongs")
    private java.util.Set<User> likedByUsers = new java.util.HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getArtist() {
        return artist;
    }

    public void setArtist(User artist) {
        this.artist = artist;
    }

    public Long getAlbumId() {
        return album != null ? album.getId() : null;
    }

    public void setAlbumId(Long albumId) {
        if (albumId != null) {
            this.album = new Album();
            this.album.setId(albumId);
        } else {
            this.album = null;
        }
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public java.sql.Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(java.sql.Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public java.sql.Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.sql.Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public java.sql.Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(java.sql.Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Long getPlayCount() {
        return playCount != null ? playCount : 0L;
    }

    public void setPlayCount(Long playCount) {
        this.playCount = playCount;
    }

    public java.util.Set<User> getLikedByUsers() {
        return likedByUsers;
    }

    public void setLikedByUsers(java.util.Set<User> likedByUsers) {
        this.likedByUsers = likedByUsers;
    }
}
