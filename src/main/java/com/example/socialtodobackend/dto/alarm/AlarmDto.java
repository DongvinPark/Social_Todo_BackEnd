package com.example.socialtodobackend.dto.alarm;

import com.example.socialtodobackend.persist.AlarmEntity;
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
public class AlarmDto {

    private Long alarmEntityPKId;

    private Long alarmReceiveUserPKId;

    private Long alarmSenderUserPKId;

    private Long numberOfUsersRelatedThisAlarm;

    private Long relatedPublicTodoPKId;

    private String alarmType;

    private String alarmContent;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;


    public static AlarmDto fromEntity(AlarmEntity alarmEntity){
        return AlarmDto.builder()
            .alarmEntityPKId(alarmEntity.getId())
            .alarmReceiveUserPKId(alarmEntity.getAlarmReceiverUserId())
            .alarmSenderUserPKId(alarmEntity.getAlarmSenderUserId())
            .numberOfUsersRelatedThisAlarm(alarmEntity.getNumberOfPeopleRelatedToAlarm())
            .relatedPublicTodoPKId(alarmEntity.getRelatedPublicTodoPKId())
            .alarmType(alarmEntity.getAlarmType().toString())
            .alarmContent(alarmEntity.getAlarmContent())
            .createdAt(alarmEntity.getCreatedAt())
            .modifiedAt(alarmEntity.getModifiedAt())
            .build();
    }

}
