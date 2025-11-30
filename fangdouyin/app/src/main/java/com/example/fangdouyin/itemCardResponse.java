package com.example.fangdouyin;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class itemCardResponse {
    @SerializedName("code") // 状态码：200 成功，其他失败
    private int code;

    @SerializedName("data") // 图片列表数据
    private List<RecyclerStaggeredGridBean> data;

    public int getCode() {
        return code;
    }

    public List<RecyclerStaggeredGridBean> getData() {
        return data;
    }
}
