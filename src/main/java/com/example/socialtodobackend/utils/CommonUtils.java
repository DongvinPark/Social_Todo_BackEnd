package com.example.socialtodobackend.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommonUtils {

    /**
     * 한 명의 유저가 팔로우 할 수 있는 다른 유저의 수는 최대 5,000명 까지다.
     * 그러나 한 명의 유저하 확보할 수 이는 팔로워의 숫자에는 제한이 없다.
     * */
    public static final int FOLLOW_LIMIT = 5_000;


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
    public static LocalDateTime stringToDate(String dateString){
        String[] dateInfos = dateString.split("-");
        return LocalDateTime.of(
            Integer.parseInt(dateInfos[0]),
            Integer.parseInt(dateInfos[1]),
            Integer.parseInt(dateInfos[2]),
            23,
            59,
            59
        );
    }

}
