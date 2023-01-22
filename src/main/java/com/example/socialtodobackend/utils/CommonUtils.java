package com.example.socialtodobackend.utils;

import com.example.socialtodobackend.exception.SingletonException;
import java.time.LocalDate;

/**
 * 서비스 운영에 필요한 각종 상수, 유틸리티 및 검증 메서드들이 모여 있다.
 * */
public class CommonUtils {

    /**
     * 한 명의 유저가 팔로우 할 수 있는 다른 유저의 수는 최대 5,000명 까지다.
     * 그러나 한 명의 유저가 확보할 수 있는 팔로워의 숫자에는 제한이 없다.
     * */
    public static final long FOLLOW_LIMIT = 5_000;

    public static final int PAGE_SIZE = 20;
    public static final int TODO_CONTENT_LENGTH_LIMIT = 100;
    public static final int  STATUS_MESSAGE_LENGTH_LIMIT = 50;
    public static final int LONGEST_DEADLINE_DATE_LIMIT = 365;
    public static final int JWT_VALID_DAY_LENGTH = 7;
    public static final int USER_FOLLOWEE_LIST_VALID_DAY_LENGTH = 1;
    public static final int NUMBER_OF_EXPECTED_MAX_REQUEST = 3350;


    /**
     * 투두 컨텐츠의 길이는 0자 이상 100자 이내여야 한다.
     * 오늘 만들어진 투두 아이템의 데드라인 날짜는 가장 빠르게 설정할 경우 오늘로 설정할 수 있고,
     * 가장 늦게 설정할 경우, 오늘로부터 365일이 지난 날짜까지 설정할 수 있다.
     * 그 이외의 디데이 설정은 전부 무효처리 한다.
     * */
    public static void validateContentLengthAndDeadlineDate(String privateTodoContent, LocalDate dateInput){
        if(privateTodoContent != null){
            if(privateTodoContent.length() == 0){
                throw SingletonException.ZERO_CONTENT_LENGTH;
            }
            if(privateTodoContent.length() > TODO_CONTENT_LENGTH_LIMIT){
                throw SingletonException.CONTENT_LENGTH_TOO_LONG;
            }
        }
        if(dateInput.isBefore(LocalDate.now() ) ){
            throw SingletonException.CANNOT_SET_TODO_DEADLINE_ON_PAST;
        }
        if(dateInput.isAfter( LocalDate.now().plusDays(LONGEST_DEADLINE_DATE_LIMIT) )){
            throw SingletonException.CANNOT_SET_TODO_DEADLINE_AFTER_365DAYS;
        }
    }

    /**
     * 다른 유저가 '나'를 팔로우 했을 때, '나'에게 "? 님이 팔로우 했습니다." 라는 알림을 준다.
     * */
    public static String makeAlarmMessageWhenGetNewFollower(String followerNickname){
        return followerNickname + " 님이 팔로우 했습니다.";
    }

    /**
     * 내가 다른 유저를 팔로우 했을 때 나에게 "? 님을 팔로우 했습니다." 라는 알림을 준다.
     * */
    public static String makeAlarmMessageWhenFollowedOtherUser(String followeeNickName){
        return followeeNickName + " 님을 팔로우 했습니다.";
    }

    /**
     * 특정 유저가 잔소리를 했을 경우 "? 명이 잔소리를 해줬습니다."라는 알림이 온다.
     * */
    public static String makeNagAlarmMessage(){
        return " 명이 잔소리를 해줬습니다.";
    }

    /**
     * 특정 유저가 응원을 했을 경우 "? 명이 응원을 해줬습니다."라는 알림이 온다.
     * */
    public static String makeSupportAlarmMessage(){
        return " 명이 응원을 해줬습니다.";
    }

}

























