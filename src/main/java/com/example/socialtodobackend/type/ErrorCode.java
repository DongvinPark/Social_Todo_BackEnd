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
    CANNOT_SET_PRIVATE_TODO_DEADLINE_AFTER_365DAYS("프라이빗 투두 아이템의 데드라인 날짜는 오늘로부터 365일이 지난 날짜 이내여야 합니다.");

    private final String description;
}
