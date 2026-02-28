package com.rev.app.dto;

import java.util.List;

public class PlaylistDTO {

    private Long id;
    private String name;
    private String description;
    private List<Long> songIds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Long> getSongIds() {
        return songIds;
    }

    public void setSongIds(List<Long> songIds) {
        this.songIds = songIds;
    }
}
