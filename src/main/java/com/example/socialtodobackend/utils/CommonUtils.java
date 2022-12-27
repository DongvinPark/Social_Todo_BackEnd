package com.example.socialtodobackend.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class CommonUtils {

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
