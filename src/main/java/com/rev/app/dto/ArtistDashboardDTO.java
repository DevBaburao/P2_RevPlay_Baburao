package com.rev.app.dto;

public class ArtistDashboardDTO {
    private Long totalSongs;
    private Long totalPlays;
    private Long totalFavorites;
    private SongPlayCountDTO mostPlayedSong;

    public ArtistDashboardDTO() {
    }

    public Long getTotalSongs() {
        return totalSongs;
    }

    public void setTotalSongs(Long totalSongs) {
        this.totalSongs = totalSongs;
    }

    public Long getTotalPlays() {
        return totalPlays;
    }

    public void setTotalPlays(Long totalPlays) {
        this.totalPlays = totalPlays;
    }

    public Long getTotalFavorites() {
        return totalFavorites;
    }

    public void setTotalFavorites(Long totalFavorites) {
        this.totalFavorites = totalFavorites;
    }

    public SongPlayCountDTO getMostPlayedSong() {
        return mostPlayedSong;
    }

    public void setMostPlayedSong(SongPlayCountDTO mostPlayedSong) {
        this.mostPlayedSong = mostPlayedSong;
    }
}
