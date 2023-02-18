package com.example.socialtodobackend.dto.user;

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
public class UserDto {

    private Long id;

    private String nickname;

    private String emailAddr;

    private String statusMessage;

    private LocalDateTime registeredAt;

    public static UserDto fromEntity(UserEntity userEntity){
        return UserDto.builder()
            .id(userEntity.getId())
            .nickname(userEntity.getNickname())
            .emailAddr(userEntity.getEmailAddr())
            .statusMessage(userEntity.getStatusMessage())
            .registeredAt(userEntity.getRegisteredAt())
            .build();
    }

}
