package com.example.socialtodobackend.web.controller;

import com.example.socialtodobackend.dto.APIDataResponse;
import com.example.socialtodobackend.dto.user.UserDto;
import com.example.socialtodobackend.service.AlarmService;
import com.example.socialtodobackend.service.NagService;
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
public class NagController {

    private final NagService nagService;
    private final AlarmService alarmService;


    @PutMapping("/create/nag")
    public void pressNag(
        @AuthenticationPrincipal Long nagSentUserPKId,
        @RequestParam Long publicTodoPKId,
        @RequestParam Long todoAuthorUserPKId
    ){
        //잔소리 누르기 처리 이벤트를 카프카 이벤트로 프로듀스 한다.
        //결국 여기서 바로 알림 잔소리 처리 이벤트 프로듀스 하면서도 컨슘도 해야 한다.
        //그러고나서, 잔소리 누르기 이벤트 컨슈머가 무슨 일을 해야 하는지를 정의해주면 된다.
        /**
         * 특정 투두 아이템에 응원/잔소리 이벤트가 발생했을 때 컨슈머 측에서 해야 하는 일은 다음과 같다.
         *
         * 그후, 응원/잔소리를 누른 유저를 유저 리포지토리에서 찾아낸다. 그렇게 찾아낸 유저를 이용해서 응원/잔소리 리포지토리에 나중에 적용될
         * Join 쿼리를 위한 엔티티 저장에 활용한다.
         * */
        nagService.addNag(nagSentUserPKId, publicTodoPKId);

        //해당 잔소리 누르기 이벤트에 대한 알림 이벤트로 카프카에 프로듀스 한다.
        /**
         * 응원/잔소리 이벤트의 컨슈머가 해야 하는 일은 레디스를 거칠 필요가 없으므로 alarmService.sendNagInfoAlarm()에서 하는 일을
         * 수정하지 않고 그대로 처리하면 된다.
         * */
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




























