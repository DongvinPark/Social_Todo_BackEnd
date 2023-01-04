package com.example.socialtodobackend.controller;

import com.example.socialtodobackend.dto.FollowDto;
import com.example.socialtodobackend.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;
    private final AlarmService alarmService;


    @GetMapping("/get/follower/{userPKId}")
    public List<UserFollowInfoDto> getAllFollowersOfUser(
        @PathVariable Long userPKId
    ){
        return followService.getFollowers(userPKId);
    }



    @GetMapping("/get/followee/{userPKId}")
    public List<UserFollowInfoDto> getAllFolloweeUsers(
        @PathVariable Long userPKId
    ){
        return followService.getFollowees(userPKId);
    }




    @PostMapping("/follow")
    public void createFollow(
        @RequestBody @Valid FollowDto followDto
    ){
        //팔로우 정보를 저장한다.
        boolean result = followService.addFollowInfo(followDto);

        if(result){
            alarmService.sendFollowInfoAlarm(followDto);
        }
        //팔로우 정보를 저장하는 것이 성공했을 때만, 팔로우 신청한 사람과 팔로우를 받아준 사람에게 알림을 전송한다.
    }


    /**
     * 프런트 엔드를 보고 있는 유저의 입장에서는 일단 자신이 팔로우하고 있는 유저의 리스트를 쭉 확인할 것이다.
     * 그 리스트의 요소들은 UserFollowInfoDto에 정의한 대로 구성돼 있다.
     * 각각의 요소들은 해당 요소들이 FollowEntity와 매핑된 DB 테이블 상에서의 주키의 값이 무엇인지의 정보가 pkIdInFollowEntity 필드로 담겨 있으므로, 언파롤우를 할 때는 pkIdInFollowEntity 번호 하나만 넘겨주면 팔로우 관계를 해지하는 것이 가능하다.
     * */
    @DeleteMapping("/unfollow/{followRepositoryPKId}")
    public void unfollow(
        @PathVariable Long followRepositoryPKId
    ){
        followService.deleteFollowInfo(followRepositoryPKId);
    }


}
