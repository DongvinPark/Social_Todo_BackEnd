package com.example.socialtodobackend.web.controller;

import com.example.socialtodobackend.dto.APIDataResponse;
import com.example.socialtodobackend.dto.user.UserDto;
import com.example.socialtodobackend.service.AlarmService;
import com.example.socialtodobackend.service.SupportService;
import com.example.socialtodobackend.utils.CommonUtils;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SupportController {

    private final SupportService supportService;
    private final AlarmService alarmService;


    /**
     * pressSupport()메서드 내의 서비스 계층 메서드 두 개에는 대량의 트래픽에 대한 비동기 처리를 위해서
     * @Async 를 적용하였음.
     * */
    @PutMapping("/create/support")
    @ApiOperation("특정 공개 투두 아이템에 대한 응원 버튼 누르기 처리 - @Async 적용함")
    public void pressSupport(
        @AuthenticationPrincipal Long supportSentUserPKId,
        @RequestParam Long publicTodoPKId,
        @RequestParam Long todoAuthorUserPKId
    ){
        supportService.addSupport(supportSentUserPKId, publicTodoPKId);
        alarmService.sendSupportInfoAlarm(supportSentUserPKId, publicTodoPKId, todoAuthorUserPKId);
    }



    @PutMapping("/cancel/support")
    @ApiOperation("눌렀던 응원을 취소함")
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
    @ApiOperation("특정 공개 투두 아이템에 대하여 응원 눌러준 유저 리스트 확인")
    public APIDataResponse< List<UserDto> > getSupportSentUsers(
        @RequestParam Long publicTodoPKId, @RequestParam int pageNumber
    ){
        PageRequest pageRequest = PageRequest.of(pageNumber, CommonUtils.PAGE_SIZE);
        return APIDataResponse.of(
            supportService.getAllSupportSentUsers(publicTodoPKId, pageRequest)
        );
    }

}
