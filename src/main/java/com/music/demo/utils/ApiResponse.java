package com.music.demo.utils;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * @param <T> 返回的data类型
 */
@Getter
@Setter
public class ApiResponse<T> {
    // Json返回装载的数据
    private T data;
    // 返回状态码
    private int status;
    // 状态消息
    private String msg;


    public ApiResponse(T data, int status, String msg) {
        this.data = data;
        this.status = status;
        this.msg = msg;
    }

    // 响应成功并且返回数据
    public static<T> ApiResponse<T> success(T data){
        return new ApiResponse<T>(data,200,"success");
    }

    // 响应成功并且返回数据和消息
    public static<T> ApiResponse<T> success(T data, String msg){
        return new ApiResponse<T>(data,200, msg);
    }

    // 响应成功并返回消息，不装载数据
    public static<T> ApiResponse<T> success(String msg){
        return new ApiResponse<T>(null,200, msg);
    }

    public static<T> ApiResponse<T> success(){
        return new ApiResponse<T>(null,200,"success");
    }

    public static<T> ApiResponse<T> error(String msg){
        return new ApiResponse<T>(null,500, msg);
    }

    public static<T> ApiResponse<T> error(){
        return new ApiResponse<T>(null,500,"error");
    }

    public static<T> ApiResponse<T> unauthorized(String msg){
        return new ApiResponse<T>(null,401, msg);
    }
}
