package com.example.socialtodobackend.web.controller;

import com.example.socialtodobackend.dto.APIDataResponse;
import com.example.socialtodobackend.dto.follow.UserFollowInfoDto;
import com.example.socialtodobackend.service.AlarmService;
import com.example.socialtodobackend.service.FollowService;
import com.example.socialtodobackend.utils.CommonUtils;
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
    public APIDataResponse< List<UserFollowInfoDto> > getAllFollowersOfUser(
        @AuthenticationPrincipal Long userPKId, @RequestParam int pageNumber
    ){
        PageRequest pageRequest = PageRequest.of(pageNumber, CommonUtils.PAGE_SIZE);
        return APIDataResponse.of( followService.getFollowers(userPKId, pageRequest) );
    }



    @GetMapping("/followees")
    public APIDataResponse< List<UserFollowInfoDto> >  getAllFolloweeUsers(
        @AuthenticationPrincipal Long userPKId,
        @RequestParam int pageNumber
    ){
        PageRequest pageRequest = PageRequest.of(pageNumber, CommonUtils.PAGE_SIZE);
        return APIDataResponse.of( followService.getFollowees(userPKId, pageRequest) );
    }




    @PostMapping("/follow")
    public void createFollowRelation(
        @AuthenticationPrincipal Long followSentUserPKId,
        @RequestParam Long followRelationTargetUserPKId
    ){
        //팔로우 정보를 저장한다.
        /**
         * 그러면서 동시에 레디스의 {유저 주키 : 그 유저가 팔로우한 다른 유저들의 주키 아이디 리스트} 이 키 밸류 쌍을 찾아내서
         * 새롭게 추가된 팔로우 타겟 유저를 레디스 캐시 리스트에 추가해 둬야 한다.
         * 레디스의 리스트는 LinkedList 자료구조이기 때문에 O(1)시간에 가능하다.
         * */
        followService.addFollowInfo(followSentUserPKId, followRelationTargetUserPKId);

        //그 후 알림을 보낸다.
        alarmService.sendFollowInfoAlarm(followSentUserPKId, followRelationTargetUserPKId);
    }


    /**
     * 언팔로우 요청을 보내는 사용자의 주키 아이디과,
     * 해당 사용자가 언팔로우 하고자 하는 대상 사용자의 주키 아이디를 @RequestParam으로 전달한다.
     * */
    @DeleteMapping("/delete/follow-relation")
    public void deleteFollowRelation(
        @AuthenticationPrincipal Long requestUserPKId,
        @RequestParam Long unfollowTargetUserPKId
    ){
        /**
         * 그 후, 레디스의 {유저 주키 : 그 유저가 팔로우한 다른 유저들의 주키 아이디 리스트} 이 키 밸류 쌍을 찾아내서
         * 새롭게 추가된 팔로우 타겟 유저를 레디스 캐시 리스트에서 삭제해야 한다.
         * 레디스의 리스트는 LinkedList 자료구조이기 때문에 O(N)시간이 소요된다.
         * */
        followService.deleteFollowInfo(requestUserPKId, unfollowTargetUserPKId);
    }


}






















