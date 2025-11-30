package com.example.fangdouyin;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.text.DecimalFormat;

public class RecyclerStaggeredGridBean {
    @SerializedName("ItemId")
    private long itemId;
    @SerializedName("Title")
    private String title;
    @SerializedName("HeadUrl")
    private String headId;
    @SerializedName("UserName")
    private String name;
    @SerializedName("LikeNum")
    private long likeNum;
    @SerializedName("ImageUrl")
    private String imageId;

    @SerializedName("ImageWidth")
    private int imageWidth;
    @SerializedName("ImageHeight")
    private int imageHeight;

    private int showHeight = -1;

    private boolean isLiked= false;

    // 本地存储路径（保存后赋值）
    private String localPostImagePath; // 本地内容图路径
    private String localAvatarPath;    // 本地头像路径

    // 图片加载状态（避免未加载完成就保存）
    private boolean isPostImageLoaded = false;
    private boolean isAvatarLoaded = false;




    public long getItemId(){
        return itemId;
    }
    public void setItemId(long itemId){
        this.itemId=itemId;
    }

    public String getImageId() {
        return imageId;
    }
    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHeadId() {
        return headId;
    }

    public void setHeadId(String headId) {
        this.headId = headId;
    }
    DecimalFormat df = new DecimalFormat("0.0");

    public String getLikeNum() {
        if (likeNum>=10000){
            return String.valueOf(df.format(1.0*likeNum/10000))+"万";
        }else {
            return String.valueOf(likeNum);
        }
    }

    public long getLongLikeNum(){
        return likeNum;
    }

    public void setLikeNum(long likeNum) {
        this.likeNum = likeNum;
    }

    public boolean getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(boolean isLiked) {
        this.isLiked = isLiked;
    }




    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public int getShowHeight() {
        return showHeight;
    }

    public void setShowHeight(int showHeight) {
        this.showHeight = showHeight;
    }

    public boolean isFirst(){
        return showHeight == -1;
    }


    public void incrementLikeCount() {
        this.likeNum++;
    }


    public void decrementLikeCount() {
        this.likeNum--;
    }

    public String getLocalAvatarPath() {
        return localAvatarPath;
    }

    public void setLocalAvatarPath(String localAvatarPath) {
        this.localAvatarPath = localAvatarPath;
    }

    public String getLocalPostImagePath() {
        return localPostImagePath;
    }

    public void setLocalPostImagePath(String localPostImagePath) {
        this.localPostImagePath = localPostImagePath;
    }

    public boolean isPostImageLoaded() {
        return isPostImageLoaded;
    }

    public void setPostImageLoaded() {
        Log.d("test","标识符变更");
        isPostImageLoaded = true;
    }

    public boolean isAvatarLoaded() {

        return isAvatarLoaded;
    }

    public void setAvatarLoaded() {
        Log.d("test","标识符变更");
        isAvatarLoaded = true;
    }
}
