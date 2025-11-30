package com.example.fangdouyin;

import java.text.DecimalFormat;

public class RecyclerGridBean {
    private String imageId;
    private long likeNum;

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
    DecimalFormat df = new DecimalFormat("0.0");
    public String getLikeNum() {
        if (likeNum>=10000){
            return String.valueOf(df.format(1.0*likeNum/10000))+"万";
        }else {
            return String.valueOf(likeNum);
        }
    }

    public void setLikeNum(long likeNum) {
        this.likeNum = likeNum;
    }
}
