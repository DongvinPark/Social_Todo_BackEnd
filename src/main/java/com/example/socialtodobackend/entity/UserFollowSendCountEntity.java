package com.example.socialtodobackend.entity;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class UserFollowSendCountEntity {

    /**
     * UserEntity의 주키 아이디와 동일해야 하기 때문에
     * @GeneratedValue를 쓰지 않는다.
     * 대신 UserEntity가 새로 DB에 저장될 때마다 UserEntity의 주키와 동일한 값을 갖도록 빼먹지 않고 매핑해줘야 한다.
     * UserEntity가 삭제될 때도 해당 유저에 매핑되는 UserFollowSendCountEntity를 항상 같이 삭제해 줘야 한다.
     * */
    @Id
    private Long id_dependsOnFollowSentUserPK;

    private Long userFollowSendCount;

}
