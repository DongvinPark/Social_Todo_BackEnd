package com.example.socialtodobackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

public class UserFollowInfoDto {
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public class UserFollowInfoDto {

        private Long id;

        private Long pkIdInFollowEntity;

        private String nickname;

        private String statusMessage;

        private String registeredAt;



        public static UserFollowInfoDto fromEntity(UserEntity userEntity, FollowEntity followEntity){
            return UserFollowInfoDto.builder()
                .id(userEntity.getId())
                .pkIdInFollowEntity(followEntity.getId())
                .nickname(userEntity.getNickname())
                .statusMessage(userEntity.getStatusMessage())
                .registeredAt(CommonUtils.dateToString(userEntity.getRegisteredAt()))
                .build();
        }

    }
}
