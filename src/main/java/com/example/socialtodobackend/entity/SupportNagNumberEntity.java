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
public class SupportNagNumberEntity {

    /**
     * PublicTodoEntity의 주키 아이디와 동일해야 하기 때문에
     * @GeneratedValue 를 쓰지 않는다.
     * 대신 공개 투두가 생성될 때마다 이쪽 엔티티와 리포지토리에도 주키를 공개 투두 아이템의 주키로
     * 매핑해주는 작업을 빼먹지 않고 수행해야 한다.
     * PublicTodoEntity가 삭제될 때, 해당 엔티티에 대응되는 SupportNagNumberEntity도 항상 같이 삭제해 줘야 한다.
     * */
    @Id
    private Long id_DependsOnPublicTodoPK;

    private Long authorUserPKId;

    private Long numberOfSupport;
    private Long numberOfNag;

}
