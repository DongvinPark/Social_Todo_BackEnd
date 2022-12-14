package com.example.socialtodobackend.controller;

import com.example.socialtodobackend.dto.AlarmDto;
import com.example.socialtodobackend.service.AlarmService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;


    @GetMapping("get/alarms/{userPKId}")
    public List<AlarmDto> getAllAlarm(
        @PathVariable Long userPKId
    ){
        return alarmService.getAlarmList(userPKId);
    }



    /*
    알림을 보내는 기능은 유저가 소셜 투두 서비스 내에서 응원,잔소리,팔로우,할일완료 등의 행동을
    했을 때 백엔드에서 자동으로 수행되는 기능이며, 유저가 알림을 보내는 버튼을 프런트엔드에서
    명시적으로 제공 받는 것은 아니므로 알림을 보내는 별도의 메서드를 컨트롤러 측에서 제공하지는 않음.
    */



    @DeleteMapping("/delete/alarm")
    public List<AlarmDto> deleteOneAlarm(
        @RequestBody AlarmDto alarmDto
    ){
        alarmService.removeOneAlarm(alarmDto.getAlarmEntityPKId());
        return alarmService.getAlarmList(alarmDto.getAlarmReceiveUserPKId());
    }



    @DeleteMapping("/delete/alarms/{userPKId}")
    public List<AlarmDto> deleteAllAlarms(
        @PathVariable Long userPKId
    ){
        return alarmService.removeAllAlarm(userPKId);
    }

}
























