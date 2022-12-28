package com.example.socialtodobackend.dto;

import com.example.socialtodobackend.entity.UserEntity;
import com.example.socialtodobackend.utils.CommonUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private Long id;

    private String nickname;

    private String statusMessage;

    private String registeredAt;

    private Long numberOfFollowedUsers;


    public static UserDto fromEntity(UserEntity userEntity){
        return UserDto.builder()
            .id(userEntity.getId())
            .nickname(userEntity.getNickname())
            .statusMessage(userEntity.getStatusMessage())
            .registeredAt(CommonUtils.dateToString(userEntity.getRegisteredAt()))
            .numberOfFollowedUsers(userEntity.getNumberOfFollowedUsers())
            .build();
    }

}
