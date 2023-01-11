package com.example.socialtodobackend.dto.user;

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
public class UserDto {

    private Long id;

    private String nickname;

    private String emailAddr;

    private String statusMessage;

    private LocalDateTime registeredAt;



    /**
     * 백엔드에서 유저 정보를 프런트엔드로 넘길 때는 이메일 주소를 넘기지 말아야 한다.
     * <br><br/>
     * 이메일 주소는 로그인 시 사용되는  개인정보이기 때문에 누구에게나 공개되어도 괜찮은 정보는 아니다.
     * */
    public static UserDto fromEntity(UserEntity userEntity){
        return UserDto.builder()
            .id(userEntity.getId())
            .nickname(userEntity.getNickname())
            .statusMessage(userEntity.getStatusMessage())
            .registeredAt(userEntity.getRegisteredAt())
            .build();
    }

}
