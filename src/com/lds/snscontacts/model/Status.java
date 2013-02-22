package com.lds.snscontacts.model;

import java.io.Serializable;

public class Status implements Serializable {

    private long id;
    private String created_at;

    private String text;
    private String source;

    private String thumbnail_pic;
    private String bmiddle_pic;
    private String original_pic;

    private int reposts_count;
    private int comments_count;
    private int attitudes_count;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getThumbnail_pic() {
        return thumbnail_pic;
    }

    public void setThumbnail_pic(String thumbnail_pic) {
        this.thumbnail_pic = thumbnail_pic;
    }

    public String getBmiddle_pic() {
        return bmiddle_pic;
    }

    public void setBmiddle_pic(String bmiddle_pic) {
        this.bmiddle_pic = bmiddle_pic;
    }

    public String getOriginal_pic() {
        return original_pic;
    }

    public void setOriginal_pic(String original_pic) {
        this.original_pic = original_pic;
    }

    public int getReposts_count() {
        return reposts_count;
    }

    public void setReposts_count(int reposts_count) {
        this.reposts_count = reposts_count;
    }

    public int getComments_count() {
        return comments_count;
    }

    public void setComments_count(int comments_count) {
        this.comments_count = comments_count;
    }

    public int getAttitudes_count() {
        return attitudes_count;
    }

    public void setAttitudes_count(int attitudes_count) {
        this.attitudes_count = attitudes_count;
    }

    @Override
    public String toString() {
        return "Status [id=" + id + ", created_at=" + created_at + ", text="
                + text + ", source=" + source + ", thumbnail_pic="
                + thumbnail_pic + ", bmiddle_pic=" + bmiddle_pic
                + ", original_pic=" + original_pic + ", reposts_count="
                + reposts_count + ", comments_count=" + comments_count
                + ", attitudes_count=" + attitudes_count + "]";
    }

}
