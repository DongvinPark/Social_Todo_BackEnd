package com.example.socialtodobackend.controller;

import com.example.socialtodobackend.dto.APIDataResponse;
import com.example.socialtodobackend.dto.SupportNagDto;
import com.example.socialtodobackend.dto.UserDto;
import com.example.socialtodobackend.service.AlarmService;
import com.example.socialtodobackend.service.SupportService;
import com.example.socialtodobackend.utils.CommonUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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
        @PathVariable Long publicTodoPKId, @RequestParam int pageNumber
    ){
        PageRequest pageRequest = PageRequest.of(pageNumber, CommonUtils.PAGE_SIZE);
        return APIDataResponse.of(
            supportService.getAllSupportSentUsers(publicTodoPKId, pageRequest)
        );
    }


}
