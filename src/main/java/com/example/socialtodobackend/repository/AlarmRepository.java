package com.example.socialtodobackend.repository;

import com.example.socialtodobackend.entity.AlarmEntity;
import com.example.socialtodobackend.type.AlarmTypeCode;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlarmRepository extends JpaRepository<AlarmEntity, Long> {

    List<AlarmEntity> findAllByAlarmReceiverUserIdEquals(Long id);

    /**
     * iterator 가 달려 있는 id 반복자가 아니라, Long 타입 아이디 하나와 일치하는 모든
     * 알림들만을 제거하는 메서드이므로 혼동하면 안 됨.
     * */
    void deleteAllByAlarmReceiverUserIdEquals(Long id);

    Optional<AlarmEntity> findAlarmEntityByRelatedPublicTodoPKIdEqualsAndAlarmTypeEquals(Long relatedPublicTodoPKId, AlarmTypeCode alarmTypeCode);

}
