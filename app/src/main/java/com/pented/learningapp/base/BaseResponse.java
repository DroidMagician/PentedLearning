package com.pented.learningapp.base;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * base pojo class for all incoming responses
 * contains only message and code
 * change key if needed.
 */

public class BaseResponse<T> implements Serializable {



    public String isStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @SerializedName("Status")
    @Expose
    private String status;

    public String getStatus() {
        return status;
    }

    @SerializedName("Message")
    @Expose
    private String msg;

    @SerializedName("Result")
    @Expose
    private T data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T    getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    @Override
    public String toString() {
        return "BaseResponse{" +
                ", message='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
