package com.example.socialtodobackend.controller;

import com.example.socialtodobackend.dto.FollowDto;
import com.example.socialtodobackend.dto.UserFollowInfoDto;
import com.example.socialtodobackend.service.AlarmService;
import com.example.socialtodobackend.service.FollowService;
import java.util.List;
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
        @RequestBody FollowDto followDto
    ){
        //팔로우 정보를 저장한다.
        followService.addFollowInfo(followDto);

        //팔로우 신청한 사람과 팔로우를 받아준 사람에게 알림을 전송한다.
        alarmService.sendFollowInfoAlarm(followDto);
    }



    @DeleteMapping("/unfollow/{followRepositoryPKId}")
    public void unfollow(
        @PathVariable Long followRepositoryPKId
    ){
        followService.deleteFollowInfo(followRepositoryPKId);
    }


}






















