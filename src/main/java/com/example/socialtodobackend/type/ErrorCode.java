package com.example.socialtodobackend.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // ERROR_CODE("에러메시지"), ... ; 의 형식으로 작성한다.
    INTER_SERVER_ERROR("내부 서버 오류가 발생했습니다."),
    PRIVATE_TODO_NOT_FOUND("프라이빗 투두 아이템을 찾지 못했습니다."),
    ZERO_CONTENT_LENGTH("할 일의 내용은 빈칸일 수 없습니다."),
    CONTENT_LENGTH_TOO_LONG("할 일의 내용은 100자를 초과할 수 없습니다."),
    CANNOT_SET_PRIVATE_TODO_DEADLINE_ON_PAST("프라이빗 투두 아이템의 데드라인 날짜는 오늘 이전일 수 없습니다."),
    CANNOT_SET_PRIVATE_TODO_DEADLINE_AFTER_365DAYS("프라이빗 투두 아이템의 데드라인 날짜는 오늘로부터 365일이 지난 날짜 이내여야 합니다."),
    USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    CANNOT_FOLLOW_MORE_THAN_5000_USERS("팔로우한 사람의 숫자는 5,000명을 초과할 수 없습니다."),
    HAS_NO_USER_TO_UNFOLLOW("팔로우한 유저가 0명이어서 언팔로우를 할 수 없습니다."),
    INVALID_DEADLINE_DATE_FORMAT("마감 기한 날짜의 포맷이 유효하지 않습니다. 마감 기힌 날짜 문자열은 yyyy-mm-dd 형태여야 합니다."),
    FOLLOW_INFO_NOT_FOUND("팔로우 정보를 찾을 수 없습니다."),
    ALARM_INFO_NOT_FOUND("알림 정보를 찾을 수 없습니다.");

    private final String description;
}
