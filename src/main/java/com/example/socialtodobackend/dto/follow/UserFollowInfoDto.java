package com.example.socialtodobackend.dto.follow;

import com.example.socialtodobackend.entity.FollowEntity;
import com.example.socialtodobackend.entity.UserEntity;
import java.time.LocalDateTime;
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
public class UserFollowInfoDto {

    private Long id;

    private Long pkIdInFollowEntity;

    private String nickname;

    private String statusMessage;

    private LocalDateTime registeredAt;



    public static UserFollowInfoDto fromEntity(UserEntity userEntity, FollowEntity followEntity){
        return UserFollowInfoDto.builder()
            .id(userEntity.getId())
            .pkIdInFollowEntity(followEntity.getId())
            .nickname(userEntity.getNickname())
            .statusMessage(userEntity.getStatusMessage())
            .registeredAt(userEntity.getRegisteredAt())
            .build();
    }

}
