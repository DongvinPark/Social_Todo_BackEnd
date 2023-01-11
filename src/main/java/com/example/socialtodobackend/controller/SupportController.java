package com.example.socialtodobackend.controller;

import com.example.socialtodobackend.dto.APIDataResponse;
import com.example.socialtodobackend.dto.user.UserDto;
import com.example.socialtodobackend.service.AlarmService;
import com.example.socialtodobackend.service.SupportService;
import com.example.socialtodobackend.utils.CommonUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SupportController {

    private final SupportService supportService;
    private final AlarmService alarmService;


    @PutMapping("/create/support")
    public void pressSupport(
        @AuthenticationPrincipal Long supportSentUserPKId,
        @RequestParam Long publicTodoPKId,
        @RequestParam Long todoAuthorUserPKId
    ){
        supportService.addSupport(supportSentUserPKId, publicTodoPKId);
        alarmService.sendSupportInfoAlarm(supportSentUserPKId, publicTodoPKId, todoAuthorUserPKId);
    }



    @PutMapping("/cancel/support")
    public void cancelSupport(
        @AuthenticationPrincipal Long supportSentUserPKId,
        @RequestParam Long publicTodoPKId
    ){
        supportService.undoSupport(supportSentUserPKId, publicTodoPKId);
    }



    /**
     * 회원가입을 마치고 정상적으로 로그인한 사용자라면 누구라도 확인할 수 있는 내용이므로,
     * @AuthenticationPrincipal 을 사용하면 안 된다.
     * */
    @GetMapping("/support/users")
    public APIDataResponse< List<UserDto> > getSupportSentUsers(
        @RequestParam Long publicTodoPKId, @RequestParam int pageNumber
    ){
        PageRequest pageRequest = PageRequest.of(pageNumber, CommonUtils.PAGE_SIZE);
        return APIDataResponse.of(
            supportService.getAllSupportSentUsers(publicTodoPKId, pageRequest)
        );
    }

}