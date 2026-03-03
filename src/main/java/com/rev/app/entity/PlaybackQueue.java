package com.rev.app.entity;

import java.util.ArrayList;
import java.util.List;

public class PlaybackQueue {
    private List<Long> songIds = new ArrayList<>();
    private int currentIndex = -1;
    private boolean shuffleEnabled = false;
    private RepeatMode repeatMode = RepeatMode.OFF;

    public enum RepeatMode {
        OFF, ONE, ALL
    }

    public List<Long> getSongIds() {
        return songIds;
    }

    public void setSongIds(List<Long> songIds) {
        this.songIds = songIds;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public boolean isShuffleEnabled() {
        return shuffleEnabled;
    }

    public void setShuffleEnabled(boolean shuffleEnabled) {
        this.shuffleEnabled = shuffleEnabled;
    }

    public RepeatMode getRepeatMode() {
        return repeatMode;
    }

    public void setRepeatMode(RepeatMode repeatMode) {
        this.repeatMode = repeatMode;
    }
}
