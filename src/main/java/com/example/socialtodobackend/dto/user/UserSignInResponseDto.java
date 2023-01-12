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
public class UserSignInResponseDto {

    private Long id;

    private String nickname;

    private String emailAddr;

    private String statusMessage;

    private LocalDateTime registeredAt;

    private String jwt;

    /**
     * 로그인에 성공했을 때만 해당 유저의 프런트엔드로 리턴되는 Dto 이므로,
     * 이메일 주소와 토큰 정보를 함께 넘겨준다.
     * */
    public static UserSignInResponseDto fromEntity(UserEntity userEntity, String token){
        return UserSignInResponseDto.builder()
            .id(userEntity.getId())
            .nickname(userEntity.getNickname())
            .emailAddr(userEntity.getEmailAddr())
            .statusMessage(userEntity.getStatusMessage())
            .registeredAt(userEntity.getRegisteredAt())
            .jwt(token)
            .build();
    }

}
