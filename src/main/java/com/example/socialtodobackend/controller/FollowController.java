package com.example.socialtodobackend.controller;

import com.example.socialtodobackend.dto.APIDataResponse;
import com.example.socialtodobackend.dto.follow.FollowDto;
import com.example.socialtodobackend.dto.follow.UserFollowInfoDto;
import com.example.socialtodobackend.service.AlarmService;
import com.example.socialtodobackend.service.FollowService;
import java.util.List;
import javax.validation.Valid;
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


    @GetMapping("/followers/{userPKId}")
    public APIDataResponse< List<UserFollowInfoDto> > getAllFollowersOfUser(
        @PathVariable Long userPKId
    ){
        return APIDataResponse.of( followService.getFollowers(userPKId) );
    }



    @GetMapping("/followees/{userPKId}")
    public APIDataResponse< List<UserFollowInfoDto> >  getAllFolloweeUsers(
        @PathVariable Long userPKId
    ){
        return APIDataResponse.of( followService.getFollowees(userPKId) );
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
     * 프런트 엔드를 보고 있는 유저의 입장에서는 일단 자신이 팔로우하고 있는 유저의 리스트를 쭉 확인할 것이다.
     * 그 리스트의 요소들은 UserFollowInfoDto에 정의한 대로 구성돼 있다.
     * 각각의 요소들은 해당 요소들이 FollowEntity와 매핑된 DB 테이블 상에서의 주키의 값이 무엇인지의 정보가 pkIdInFollowEntity 필드로 담겨 있으므로, 언팔로우를 할 때는 pkIdInFollowEntity 번호 하나만 넘겨주면 팔로우 관계를 해지하는 것이 가능하다.
     * */
    @DeleteMapping("/delete/follow-relation/{id}")
    public void deleteFollowRelation(
        @PathVariable Long id
    ){
        followService.deleteFollowInfo(id);
    }


}






















