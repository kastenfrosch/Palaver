package com.example.palaver.utils.api;

public class ApiResponse {

    private int msgType;
    private String info;
    protected Object data;

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    protected String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    protected Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
