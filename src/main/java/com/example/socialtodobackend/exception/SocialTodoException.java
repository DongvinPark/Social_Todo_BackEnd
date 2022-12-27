package com.example.socialtodobackend.exception;

import com.example.socialtodobackend.type.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SocialTodoException extends RuntimeException{

    private ErrorCode errorCode;
    private String errorMessage;

    public SocialTodoException(ErrorCode errorCode){
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }

}
