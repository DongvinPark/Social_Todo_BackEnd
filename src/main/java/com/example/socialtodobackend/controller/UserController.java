package com.example.socialtodobackend.controller;

import com.example.socialtodobackend.dto.APIDataResponse;
import com.example.socialtodobackend.dto.UserDto;
import com.example.socialtodobackend.dto.publictodo.PublicTodoDto;
import com.example.socialtodobackend.entity.UserEntity;
import com.example.socialtodobackend.repository.UserRepository;
import com.example.socialtodobackend.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final UserRepository userRepository;
    //private final FollowSendCountRepository followSendCountRepository;

    @PostMapping("/temp/signup")
    public UserDto tempAddUser(
        @RequestBody UserDto userDto
    ){
        UserEntity signUpUserEntity = userRepository.save(
            UserEntity.builder()
                .nickname(userDto.getNickname())
                .password("1111")
                .emailAddr("e@mail.com")
                .statusMessage("status~")
                .build()
        );
        /*

        ***** followSendCountRepository를 쓸 생각이 없다면, 주석 친 부분은 쓸 필요 없는 코드다.

        followSendCountRepository.save(
            followSendCountRepository.save(
                UserFollowSendCountEntity.builder()
                    //주키를 팔로우 보낸 유저의 주키 아이디와 일치시켜서 셋팅해줘야 한다!!
                    .id_dependsOnFollowSentUserPK(signUpUserEntity.getId())
                    .userFollowSendCount(0L)
                    .build()
            )
        );*/
        return UserDto.fromEntity(signUpUserEntity);
    }



    @GetMapping("/time-line/{userPKId}")
    public APIDataResponse< List<PublicTodoDto> > getTimeLine(
        @PathVariable Long userPKId
    ){
        return APIDataResponse.of(userService.makeTimeLine(userPKId));
    }



    @GetMapping("/search/users/{userNickname}")
    public APIDataResponse< List<UserDto> > searchUsers(
        @PathVariable String userNickname
    ){
        return APIDataResponse.of(userService.searchUsersByNickname(userNickname));
    }



}//end of class
