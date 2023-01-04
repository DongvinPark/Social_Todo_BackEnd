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
public class UserFollowInfoDto {

    private Long id;

    private Long pkIdInFollowEntity;

    private String nickname;

    private String statusMessage;

    private String registeredAt;



    /**
     * 회원 가입시 프런트 엔드로부터 들어오는 UserDto에서는 이메일 주소가 포함될 수 있지만, 백엔드에서 유저 정보를 프런트엔드로 넘길 때는 이메일 주소를 넘기지 말아야 한다.
     * 이메일 주소는 로그인 시 사용되는 아이디이기 때문에 누구에게나 공개되어도 괜찮은 정보는 아니다.
     * */
    public static UserDto fromEntity(UserEntity userEntity){
        return UserDto.builder()
            .id(userEntity.getId())
            .nickname(userEntity.getNickname())
            .statusMessage(userEntity.getStatusMessage())
            .registeredAt(CommonUtils.dateToString(userEntity.getRegisteredAt()))
            .build();
    }

}
