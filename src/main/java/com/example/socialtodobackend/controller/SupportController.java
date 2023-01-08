package com.example.socialtodobackend.controller;

import com.example.socialtodobackend.dto.APIDataResponse;
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
        supportService.addSupport(supportNagDto);
        alarmService.sendSupportInfoAlarm(supportNagDto);
    }



    @PutMapping("/cancel/support")
    public void cancelSupport(
        @RequestBody SupportNagDto supportNagDto
    ){
        supportService.undoSupport(supportNagDto);
    }



    @GetMapping("/support/users/{publicTodoPKId}")
    public APIDataResponse< List<UserDto> > getSupportSentUsers(
        @PathVariable Long publicTodoPKId
    ){
        return APIDataResponse.of(
            supportService.getAllSupportSentUsers(publicTodoPKId)
        );
    }


}
