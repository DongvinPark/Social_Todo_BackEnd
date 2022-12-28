package com.example.socialtodobackend.repository;

import com.example.socialtodobackend.entity.AlarmEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<AlarmEntity, Long> {

    List<AlarmEntity> findAllById(Long id);

    /**
     * iterator 가 달려 있는 id 반복자가 아니라, Long 타입 아이디 하나와 일치하는 모든
     * 알림들만을 제거하는 메서드이므로 혼동하면 안 됨.
     * */
    void deleteAllById(Long id);

}
