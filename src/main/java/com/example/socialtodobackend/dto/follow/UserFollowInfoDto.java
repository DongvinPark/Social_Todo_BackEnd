package com.example.socialtodobackend.dto.follow;

import com.example.socialtodobackend.persist.UserEntity;
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

    private String nickname;

    private String statusMessage;

    private LocalDateTime registeredAt;



    public static UserFollowInfoDto fromEntity(UserEntity userEntity){
        return UserFollowInfoDto.builder()
            .id(userEntity.getId())
            .nickname(userEntity.getNickname())
            .statusMessage(userEntity.getStatusMessage())
            .registeredAt(userEntity.getRegisteredAt())
            .build();
    }

}
