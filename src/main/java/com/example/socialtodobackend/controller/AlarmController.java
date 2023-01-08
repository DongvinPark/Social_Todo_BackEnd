package com.example.socialtodobackend.controller;

import com.example.socialtodobackend.dto.APIDataResponse;
import com.example.socialtodobackend.dto.alarm.AlarmDeleteRequest;
import com.example.socialtodobackend.dto.alarm.AlarmDto;
import com.example.socialtodobackend.service.AlarmService;
import com.example.socialtodobackend.utils.CommonUtils;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;


    @GetMapping("/alarms/{userPKId}")
    public APIDataResponse< List<AlarmDto> > getAllAlarm(
        @PathVariable Long userPKId,
        @RequestParam int pageNumber
    ){
        PageRequest pageRequest = PageRequest.of(pageNumber, CommonUtils.PAGE_SIZE);
        return APIDataResponse.of(alarmService.getAlarmList(userPKId, pageRequest));
    }



    /*
    알림을 보내는 기능은 유저가 소셜 투두 서비스 내에서 응원,잔소리,팔로우,할일완료 등의 행동을
    했을 때 백엔드에서 자동으로 수행되는 기능이며, 유저가 알림을 보내는 버튼을 프런트엔드에서
    명시적으로 제공 받는 것은 아니므로 알림을 보내는 별도의 메서드를 컨트롤러 측에서 제공하지는 않음.
    */



    /**
     * 알림 하나를 삭제하는 동작을 마친 후, 굳이 전체 알림 리스트를 다시 불러오는 동작을 할 필요는 없다.
     * */
    @DeleteMapping("/delete/alarm")
    public void deleteOneAlarm(
        @RequestBody @Valid AlarmDeleteRequest alarmDto
    ){
        alarmService.removeOneAlarm(alarmDto.getAlarmEntityPKId());
    }


    /**
     * 전체 알림을 삭제하는 동작을 마친 후, 굳이 다시 알림 리스트를 불러오는 동작을 할 필요는 없다.
     * */
    @DeleteMapping("/delete/entire-alarms/{userPKId}")
    public void deleteAllAlarms(
        @PathVariable Long userPKId
    ){
        alarmService.removeAllAlarm(userPKId);
    }

}
























