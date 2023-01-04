package com.example.socialtodobackend.controller;

import com.example.socialtodobackend.dto.SupportNagDto;
import com.example.socialtodobackend.dto.UserDto;
import com.example.socialtodobackend.service.AlarmService;
import com.example.socialtodobackend.service.NagService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NagController {

    private final NagService nagService;
    private final AlarmService alarmService;


    @PutMapping("/create/nag")
    public void pressNag(
        @RequestBody @Valid SupportNagDto supportNagDto
    ){
        boolean result = nagService.addNag(supportNagDto);
        //잔소리 수치 +1이 성공했을 때만 알림을 보내야 한다.
        if(result){
            alarmService.sendNagInfoAlarm(supportNagDto);
        }
    }



    @PutMapping("/cancel/nag")
    public void cancelNag(
        @RequestBody @Valid SupportNagDto supportNagDto
    ){
        nagService.undoNag(supportNagDto);
    }



    @GetMapping("/get/nag/users/{publicTodoPKId}")
    public List<UserDto> getNagSentUsers(
        @PathVariable Long publicTodoPKId
    ){
        return nagService.getAllNagSentUsers(publicTodoPKId);
    }

}




























