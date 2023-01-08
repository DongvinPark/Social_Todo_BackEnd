package com.example.socialtodobackend.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 서비스 운영에 필요한 각종 상수 및 유틸리티 메서드들이 모여 있다.
 * 팔로우 수 제한, 날짜와 스트링 간 변환, 알림용 메시지 작성 등의 메서들이다.
 * */
public class CommonUtils {

    /**
     * 한 명의 유저가 팔로우 할 수 있는 다른 유저의 수는 최대 5,000명 까지다.
     * 그러나 한 명의 유저가 확보할 수 있는 팔로워의 숫자에는 제한이 없다.
     * */
    public static final long FOLLOW_LIMIT = 5_000;

    public static final int PAGE_SIZE = 1;


    /**
     * 자바의 LocalDateTime의 기본 형태를
     * "yyyy-mm-dd" 포맷의 스트링으로 바꿔서 리턴한다.
     * */
    public static String dateToString(LocalDateTime inputDate){
        return inputDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * "yyyy-mm-dd" 포맷의 스트링을 LocalDateTime 객체로 변환하여 리턴한다.
     * */
    public static LocalDate stringToDate(String dateString){
        String[] dateInfos = dateString.split("-");
        return LocalDate.of(
            Integer.parseInt(dateInfos[0]),
            Integer.parseInt(dateInfos[1]),
            Integer.parseInt(dateInfos[2])
        );
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

























