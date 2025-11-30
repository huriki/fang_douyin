package com.example.fangdouyin;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "item_card")
public class item_card {
    @PrimaryKey(autoGenerate = false)
    private long itemId;

    private String title;
    private String name;
    private long likeNum;

    private String imageId;
    private int imageWidth;
    private int imageHeight;

    private String headId;

    private String imageLocalPath;
    private String avatarLocalPath;

    public item_card(long itemId, String title, String name, long likeNum, String imageId, int imageWidth, int imageHeight, String headId) {

        this.itemId = itemId;
        this.title = title;
        this.name = name;
        this.likeNum = likeNum;
        this.imageId = imageId;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.headId = headId;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(long likeNum) {
        this.likeNum = likeNum;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
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

    public String getHeadId() {
        return headId;
    }

    public void setHeadId(String headId) {
        this.headId = headId;
    }

    public String getImageLocalPath() {
        return imageLocalPath;
    }

    public void setImageLocalPath(String imageLocalPath) {
        this.imageLocalPath = imageLocalPath;
    }

    public String getAvatarLocalPath() {
        return avatarLocalPath;
    }

    public void setAvatarLocalPath(String avatarLocalPath) {
        this.avatarLocalPath = avatarLocalPath;
    }
}
