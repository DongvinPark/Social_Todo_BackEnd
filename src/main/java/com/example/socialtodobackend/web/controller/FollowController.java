package com.example.socialtodobackend.web.controller;

import com.example.socialtodobackend.dto.APIDataResponse;
import com.example.socialtodobackend.dto.follow.UserFollowInfoDto;
import com.example.socialtodobackend.service.AlarmService;
import com.example.socialtodobackend.service.FollowService;
import com.example.socialtodobackend.utils.CommonUtils;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;
    private final AlarmService alarmService;


    @GetMapping("/followers")
    @ApiOperation("특정 유저를 팔로우 하는 다른 유저들 리스트 요청")
    public APIDataResponse< List<UserFollowInfoDto> > getAllFollowersOfUser(
        @AuthenticationPrincipal Long userPKId, @RequestParam int pageNumber
    ){
        PageRequest pageRequest = PageRequest.of(pageNumber, CommonUtils.PAGE_SIZE);
        return APIDataResponse.of( followService.getFollowers(userPKId, pageRequest) );
    }



    @GetMapping("/followees")
    @ApiOperation("특정 유저가 팔로우 하는 다른 유저들 리스트 요청")
    public APIDataResponse< List<UserFollowInfoDto> >  getAllFolloweeUsers(
        @AuthenticationPrincipal Long userPKId,
        @RequestParam int pageNumber
    ){

        /**
         * 내가 팔로우 하고 있는 사람들을 찾는다. 레디스에서 바로 찾을 수 있다. 없으면 DB 보면서 레디스에도 set 한다.
         * */
        PageRequest pageRequest = PageRequest.of(pageNumber, CommonUtils.PAGE_SIZE);
        return APIDataResponse.of( followService.getFollowees(userPKId, pageRequest) );
    }




    @PostMapping("/follow")
    @ApiOperation("팔로우 관계 등록")
    public void createFollowRelation(
        @AuthenticationPrincipal Long followSentUserPKId,
        @RequestParam Long followRelationTargetUserPKId
    ){
        //팔로우 정보를 저장한다.
        followService.addFollowInfo(followSentUserPKId, followRelationTargetUserPKId);

        //그 후 알림을 보낸다.
        alarmService.sendFollowInfoAlarm(followSentUserPKId, followRelationTargetUserPKId);
    }


    /**
     * 언팔로우 요청을 보내는 사용자의 주키 아이디과,
     * 해당 사용자가 언팔로우 하고자 하는 대상 사용자의 주키 아이디를 @RequestParam으로 전달한다.
     * */
    @DeleteMapping("/delete/follow-relation")
    @ApiOperation("팔로우 관계 정보를 삭제함으로서 언팔로우 구현")
    public void deleteFollowRelation(
        @AuthenticationPrincipal Long requestUserPKId,
        @RequestParam Long unfollowTargetUserPKId
    ){
        followService.deleteFollowInfo(requestUserPKId, unfollowTargetUserPKId);
    }


}






















