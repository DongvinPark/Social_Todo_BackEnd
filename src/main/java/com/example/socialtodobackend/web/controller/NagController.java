package com.example.socialtodobackend.web.controller;

import com.example.socialtodobackend.dto.APIDataResponse;
import com.example.socialtodobackend.dto.user.UserDto;
import com.example.socialtodobackend.service.AlarmService;
import com.example.socialtodobackend.service.NagService;
import com.example.socialtodobackend.utils.CommonUtils;
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
public class NagController {

    private final NagService nagService;
    private final AlarmService alarmService;


    /**
     * pressNag() 메서드 내의 서비스 메서드 2개는 대량의 트래픽에 대한 비동기처리를 위해서
     * @Async 를 적용하였음.
     * */
    @PutMapping("/create/nag")
    public void pressNag(
        @AuthenticationPrincipal Long nagSentUserPKId,
        @RequestParam Long publicTodoPKId,
        @RequestParam Long todoAuthorUserPKId
    ) {
        nagService.addNag(nagSentUserPKId, publicTodoPKId);
        alarmService.sendNagInfoAlarm(nagSentUserPKId, publicTodoPKId, todoAuthorUserPKId);
    }



    @PutMapping("/cancel/nag")
    public void cancelNag(
        @AuthenticationPrincipal Long nagSentUserPKId,
        @RequestParam Long publicTodoPKId
    ){
        nagService.undoNag(nagSentUserPKId, publicTodoPKId);
    }



    /**
     * 회원가입을 마치고 정상적으로 로그인한 사용자라면 누구라도 확인할 수 있는 내용이므로,
     * @AuthenticationPrincipal 을 사용하면 안 된다.
     * 이때 조인 쿼리를 사용할 수 있게 하자.
     * */
    @GetMapping("/nag/users")
    public APIDataResponse< List<UserDto> > getNagSentUsers(
        @RequestParam Long publicTodoPKId,
        @RequestParam int pageNumber
    ){
        PageRequest pageRequest = PageRequest.of(pageNumber, CommonUtils.PAGE_SIZE);
        return APIDataResponse.of( nagService.getAllNagSentUsers(publicTodoPKId, pageRequest) );
    }

}




























