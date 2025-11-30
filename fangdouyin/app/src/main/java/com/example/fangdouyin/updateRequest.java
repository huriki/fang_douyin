package com.example.fangdouyin;

public class updateRequest {
    private long item_id; // item_card ID（必传）
    private String operation; // 操作类型：add（+1）/ sub（-1）（必传）

    public updateRequest(long item_id, String operation) {
        this.item_id = item_id;
        this.operation = operation;
    }

    public long getItem_id() {
        return item_id;
    }

    public String getOperation() {
        return operation;
    }
}
