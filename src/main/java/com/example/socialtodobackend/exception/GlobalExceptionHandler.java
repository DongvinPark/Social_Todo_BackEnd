package com.example.socialtodobackend.exception;

import com.example.socialtodobackend.dto.ErrorResponse;
import com.example.socialtodobackend.type.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    //SocialTodoException 처리.
    @ExceptionHandler(SocialTodoException.class)
    public ErrorResponse handleSocialTodoException(SocialTodoException e){
        log.error("{} is occurred.", e.getErrorMessage());

        return new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        log.error("MethodArgumentNotValidException is occurred.", e);
        return new ErrorResponse(ErrorCode.INVALID_REQUEST, ErrorCode.INVALID_REQUEST.getDescription());
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorResponse handleDataIntegrityViolationException(DataIntegrityViolationException e){
        log.error("DataIntegrityViolationException is occurred.", e);

        return new ErrorResponse(ErrorCode.INVALID_REQUEST, ErrorCode.INVALID_REQUEST.getDescription());
    }


    //SocialTodoException 이외의 모든 exception 처리
    public ErrorResponse handleException(Exception e){
        log.error("Exception is occurred.", e);

        return new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR,
            ErrorCode.INTERNAL_SERVER_ERROR.getDescription());
    }


}
