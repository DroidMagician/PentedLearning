package com.pented.learningapp.base;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * acts as a container of any response with array of objects
 * <pre>
 * example :
 * if the response is like below :
 * {
 *     "code" : xyz
 *     "message" : xyz
 *     "data" : [
 *         list of pojo classes
 *     ]
 * }
 *     Then, pojo class will be automatically mapped to generic param T if the response class extend BaseArrayResponse like below:
 *
 *     class MyListResponse extends BaseArrayResponse<DataModel>
 *          --your response--        --model for data key list--
 * </pre>
 *
 * @param <T> data model generated from array inside response
 */
public class BaseArrayResponse<T> implements Serializable {

    @SerializedName("Message")
    @Expose
    private String message;

    @SerializedName("total_records")
    @Expose
    int total_records;

    @SerializedName("Status")
    @Expose
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    //change key if needed
    @SerializedName("Result")
    @Expose
    private List<T> data;

    public int getTotal_records() {
        return total_records;
    }

    public void setTotal_records(int total_records) {
        this.total_records = total_records;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BaseArrayResponse{" +
                "data=" + data +
                "} " + super.toString();
    }
}
