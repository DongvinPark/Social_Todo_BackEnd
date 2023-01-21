package com.example.socialtodobackend.exception;

import com.example.socialtodobackend.type.ErrorCode;

public class SingletonException {

    public static final SocialTodoException USER_NOT_FOUND = new SocialTodoException(ErrorCode.USER_NOT_FOUND);
    public static final SocialTodoException INTERNAL_SERVER_ERROR = new SocialTodoException(ErrorCode.INTERNAL_SERVER_ERROR);
    public static final SocialTodoException ZERO_CONTENT_LENGTH = new SocialTodoException(ErrorCode.ZERO_CONTENT_LENGTH);
    public static final SocialTodoException CANNOT_SET_TODO_DEADLINE_ON_PAST = new SocialTodoException(ErrorCode.CANNOT_SET_TODO_DEADLINE_ON_PAST);
    public static final SocialTodoException HAS_NO_USER_TO_UNFOLLOW = new SocialTodoException(ErrorCode.HAS_NO_USER_TO_UNFOLLOW);
    public static final SocialTodoException FOLLOW_INFO_NOT_FOUND = new SocialTodoException(ErrorCode.FOLLOW_INFO_NOT_FOUND);
    public static final SocialTodoException PUBLIC_TODO_NOT_FOUND = new SocialTodoException(ErrorCode.PUBLIC_TODO_NOT_FOUND);
    public static final SocialTodoException CANNOT_DELETE_TIMELINE_TARGET_TODO_ITEM = new SocialTodoException(ErrorCode.CANNOT_DELETE_TIMELINE_TARGET_TODO_ITEM);
    public static final SocialTodoException CANNOT_DECREASE_NAG_NUMBER_BELLOW_ZERO = new SocialTodoException(ErrorCode.CANNOT_DECREASE_NAG_NUMBER_BELLOW_ZERO);
    public static final SocialTodoException INVALID_NICKNAME = new SocialTodoException(ErrorCode.INVALID_NICKNAME);
    public static final SocialTodoException PASSWORD_NOT_MATCH = new SocialTodoException(ErrorCode.PASSWORD_NOT_MATCH);
    public static final SocialTodoException AUTHENTICATION_FAILED = new SocialTodoException(ErrorCode.AUTHENTICATION_FAILED);
    public static final SocialTodoException PRIVATE_TODO_NOT_FOUND = new SocialTodoException(ErrorCode.PRIVATE_TODO_NOT_FOUND);
    public static final SocialTodoException CONTENT_LENGTH_TOO_LONG = new SocialTodoException(ErrorCode.CONTENT_LENGTH_TOO_LONG);
    public static final SocialTodoException CANNOT_SET_TODO_DEADLINE_AFTER_365DAYS = new SocialTodoException(ErrorCode.CANNOT_SET_TODO_DEADLINE_AFTER_365DAYS);
    public static final SocialTodoException CANNOT_FOLLOW_MORE_THAN_5000_USERS = new SocialTodoException(ErrorCode.CANNOT_FOLLOW_MORE_THAN_5000_USERS);
    public static final SocialTodoException INVALID_DEADLINE_DATE_FORMAT = new SocialTodoException(ErrorCode.INVALID_DEADLINE_DATE_FORMAT);
    public static final SocialTodoException ALARM_INFO_NOT_FOUND = new SocialTodoException(ErrorCode.ALARM_INFO_NOT_FOUND);
    public static final SocialTodoException CANNOT_UPDATE_FINISHED_PUBLIC_TODO_ITEM = new SocialTodoException(ErrorCode.CANNOT_UPDATE_FINISHED_PUBLIC_TODO_ITEM);
    public static final SocialTodoException CANNOT_DECREASE_SUPPORT_NUMBER_BELLOW_ZERO = new SocialTodoException(ErrorCode.CANNOT_DECREASE_SUPPORT_NUMBER_BELLOW_ZERO);
    public static final SocialTodoException INVALID_REQUEST = new SocialTodoException(ErrorCode.INVALID_REQUEST);
    public static final SocialTodoException NICKNAME_ALREADY_EXISTS = new SocialTodoException(ErrorCode.NICKNAME_ALREADY_EXISTS);
    public static final SocialTodoException EMAIL_ADDRESS_ALREADY_EXISTS = new SocialTodoException(ErrorCode.EMAIL_ADDRESS_ALREADY_EXISTS);
    public static final SocialTodoException STATUS_MESSAGE_TOO_LONG = new SocialTodoException(ErrorCode.STATUS_MESSAGE_TOO_LONG);

    public static final SocialTodoException REDIS_GET_OPERATION_FAILED = new SocialTodoException(ErrorCode.REDIS_GET_OPERATION_FAILED);

}
