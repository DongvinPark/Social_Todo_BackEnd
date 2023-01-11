package com.example.socialtodobackend.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class APIDataResponse<T> {
    private final boolean success;
    private final T data;
    private final String errorMessage;


    public APIDataResponse(T data) {
        this.success = true;
        this.data = data;
        this.errorMessage = "";
    }


    public static <T> APIDataResponse<T> of (T data){
        return new APIDataResponse<>(data);
    }
}
