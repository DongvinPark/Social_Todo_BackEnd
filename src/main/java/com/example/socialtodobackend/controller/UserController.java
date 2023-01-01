package com.example.socialtodobackend.controller;

import com.example.socialtodobackend.dto.UserDto;
import com.example.socialtodobackend.entity.UserEntity;
import com.example.socialtodobackend.entity.UserFollowSendCountEntity;
import com.example.socialtodobackend.repository.FollowSendCountRepository;
import com.example.socialtodobackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final FollowSendCountRepository followSendCountRepository;

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
        followSendCountRepository.save(
            followSendCountRepository.save(
                UserFollowSendCountEntity.builder()
                    //주키를 팔로우 보낸 유저의 주키 아이디와 일치시켜서 셋팅해줘야 한다!!
                    .id_dependsOnFollowSentUserPK(signUpUserEntity.getId())
                    .userFollowSendCount(0L)
                    .build()
            )
        );
        return UserDto.fromEntity(signUpUserEntity);
    }

}//end of class
