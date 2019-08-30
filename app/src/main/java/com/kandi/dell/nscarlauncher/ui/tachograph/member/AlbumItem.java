package com.kandi.dell.nscarlauncher.ui.tachograph.member;

public class AlbumItem {
    //缩略图路径
    private String mThumbnailUrl;
    //文件路径
    private String mFileName;
    //选中状态
    private boolean mChecked;

    public String getmThumbnailUrl() {
        return mThumbnailUrl;
    }

    public void setmThumbnailUrl(String mThumbnailUrl) {
        this.mThumbnailUrl = mThumbnailUrl;
    }

    public String getmFileName() {
        return mFileName;
    }

    public void setmFileName(String mFileName) {
        this.mFileName = mFileName;
    }

    public boolean ismChecked() {
        return mChecked;
    }

    public void setmChecked(boolean mChecked) {
        this.mChecked = mChecked;
    }
}
