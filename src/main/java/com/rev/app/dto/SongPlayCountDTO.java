package com.rev.app.dto;

public class SongPlayCountDTO {
    private SongDTO song;
    private Long playCount;

    public SongPlayCountDTO() {
    }

    public SongPlayCountDTO(SongDTO song, Long playCount) {
        this.song = song;
        this.playCount = playCount;
    }

    public SongDTO getSong() {
        return song;
    }

    public void setSong(SongDTO song) {
        this.song = song;
    }

    public Long getPlayCount() {
        return playCount;
    }

    public void setPlayCount(Long playCount) {
        this.playCount = playCount;
    }
}
