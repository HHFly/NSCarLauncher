package com.example.dell.nscarlauncher.ui.music.model;

import com.example.dell.nscarlauncher.base.model.BaseModel;

public class Mp3Info  extends BaseModel{
	public long id; //歌曲列表中显示的ID
	public String displayName; //显示名称
	public long duration; //歌曲时长
	public String url; //歌曲路径
	public String title;  //歌曲标题
	public String artist; //歌曲艺术家
	
	public Mp3Info() {
	}

}
