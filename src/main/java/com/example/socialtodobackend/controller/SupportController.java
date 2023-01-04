package com.example.socialtodobackend.controller;

import com.example.socialtodobackend.dto.SupportNagDto;
import com.example.socialtodobackend.dto.UserDto;
import com.example.socialtodobackend.service.AlarmService;
import com.example.socialtodobackend.service.SupportService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SupportController {

    private final SupportService supportService;
    private final AlarmService alarmService;


    @PutMapping("/create/support")
    public void pressSupport(
        @RequestBody SupportNagDto supportNagDto
    ){
        boolean result = supportService.addSupport(supportNagDto);
        //응원 수치 +1이 성공했을 때만 알림을 보내야 한다.
        if(result){
            alarmService.sendSupportInfoAlarm(supportNagDto);
        }
    }



    @PutMapping("/cancel/support")
    public void cancelSupport(
        @RequestBody SupportNagDto supportNagDto
    ){
        supportService.undoSupport(supportNagDto);
    }



    @GetMapping("/get/support/users/{publicTodoPKId}")
    public List<UserDto> getSupportSentUsers(
        @PathVariable Long publicTodoPKId
    ){
        return supportService.getAllSupportSentUsers(publicTodoPKId);
    }


}
