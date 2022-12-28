package com.example.socialtodobackend.controller;

import com.example.socialtodobackend.dto.FollowDto;
import com.example.socialtodobackend.dto.UnfollowDto;
import com.example.socialtodobackend.dto.UserDto;
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
    public List<UserDto> getAllFollowersOfUser(
        @PathVariable Long userPKId
    ){
        return followService.getFollowers(userPKId);
    }



    @GetMapping("/get/followee/{userPKId}")
    public List<UserDto> getAllFolloweeUsers(
        @PathVariable Long userPKId
    ){
        return followService.getFollowees(userPKId);
    }




    @PostMapping("/follow")
    public void createFollow(
        @RequestBody FollowDto followDto
    ){
        followService.addFollowInfo(followDto);
        alarmService.sendFollowInfoAlarm(followDto);
    }



    @DeleteMapping("/unfollow")
    public void unfollow(
        @RequestBody UnfollowDto unfollowDto
    ){
        followService.deleteFollowInfo(unfollowDto);
    }


}






















