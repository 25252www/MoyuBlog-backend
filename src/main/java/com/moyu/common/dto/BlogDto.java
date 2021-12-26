package com.moyu.common.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public class BlogDto implements Serializable {
    private Integer id;
    private String title;
    private Integer hits;
    private LocalDateTime date;

    public BlogDto() {
    }

    public BlogDto(Integer id, String title, Integer hits, LocalDateTime date) {
        this.id = id;
        this.title = title;
        this.hits = hits;
        this.date = date;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getHits() {
        return hits;
    }

    public void setHits(Integer hits) {
        this.hits = hits;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "BlogDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", hits=" + hits +
                ", date=" + date +
                '}';
    }
}
