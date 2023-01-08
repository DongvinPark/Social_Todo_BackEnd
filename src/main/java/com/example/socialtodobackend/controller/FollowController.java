package com.example.socialtodobackend.controller;

import com.example.socialtodobackend.dto.APIDataResponse;
import com.example.socialtodobackend.dto.follow.FollowDto;
import com.example.socialtodobackend.dto.follow.UserFollowInfoDto;
import com.example.socialtodobackend.service.AlarmService;
import com.example.socialtodobackend.service.FollowService;
import com.example.socialtodobackend.utils.CommonUtils;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;
    private final AlarmService alarmService;


    @GetMapping("/followers/{userPKId}")
    public APIDataResponse< List<UserFollowInfoDto> > getAllFollowersOfUser(
        @PathVariable Long userPKId, @RequestParam int pageNumber
    ){
        PageRequest pageRequest = PageRequest.of(pageNumber, CommonUtils.PAGE_SIZE);
        return APIDataResponse.of( followService.getFollowers(userPKId, pageRequest) );
    }



    @GetMapping("/followees/{userPKId}")
    public APIDataResponse< List<UserFollowInfoDto> >  getAllFolloweeUsers(
        @PathVariable Long userPKId,
        @RequestParam int pageNumber
    ){
        PageRequest pageRequest = PageRequest.of(pageNumber, CommonUtils.PAGE_SIZE);
        return APIDataResponse.of( followService.getFollowees(userPKId, pageRequest) );
    }




    @PostMapping("/follow")
    public void createFollowRelation(
        @RequestBody @Valid FollowDto followDto
    ){
        //팔로우 정보를 저장한다.
        followService.addFollowInfo(followDto);

        alarmService.sendFollowInfoAlarm(followDto);
    }


    /**
     * 언팔로우 요청을 보내는 사용자의 주키 아이디과,
     * 해당 사용자가 언팔로우 하고자 하는 대상 사용자의 주키 아이디를 @RequestParam으로 전달한다.
     * */
    @DeleteMapping("/delete/follow-relation")
    public void deleteFollowRelation(
        @RequestParam Long requestUserPKId,
        @RequestParam Long unfollowTargetUserPKId
    ){
        followService.deleteFollowInfo(requestUserPKId, unfollowTargetUserPKId);
    }


}






















