package com.kandi.dell.nscarlauncher.ui.music.model;

import com.kandi.dell.nscarlauncher.base.model.BaseModel;
import com.kandi.dell.nscarlauncher.db.annotation.Table;

@Table(name="tb_fav")
public class Mp3Info  extends BaseModel{
    public long id; //歌曲列表中显示的ID
    public String displayName; //显示名称
    public long duration; //歌曲时长
    public String url; //歌曲路径
    public String title;  //歌曲标题
    public String artist; //歌曲艺术家

    public Mp3Info() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Mp3Info(Mp3Info data) {
        this.id = data.id;
        this.displayName = data.displayName;
        this.duration = data.duration;
        this.url = data.url;
        this.title = data.title;
        this.artist = data.artist;
    }
}
