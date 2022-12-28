package com.example.socialtodobackend.dto;

import com.example.socialtodobackend.entity.AlarmEntity;
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
public class AlarmDto {

    private Long id;

    private Long alarmReceiveUserPKId;

    private Long alarmSenderUserPKId;

    private Long numberOfUsersRelatedThisAlarm;

    private String alarmContent;

    private Long relatedPublicTodoPKId;//

    private String alarmType;//

    private String createdAt;

    private String modifiedAt;


    public static AlarmDto fromEntity(AlarmEntity alarmEntity){
        return AlarmDto.builder()
            .id(alarmEntity.getId())
            .alarmReceiveUserPKId(alarmEntity.getAlarmReceiverUserId())
            .alarmSenderUserPKId(alarmEntity.getAlarmSenderUserId())
            .numberOfUsersRelatedThisAlarm(alarmEntity.getNumberOfPeopleRelatedToAlarm())
            .alarmContent(alarmEntity.getAlarmContent())
            .relatedPublicTodoPKId(alarmEntity.getRelatedPublicTodoPKId())
            .alarmType(alarmEntity.getAlarmType().toString())
            .createdAt(CommonUtils.dateToString(alarmEntity.getCreatedAt()))
            .modifiedAt(CommonUtils.dateToString(alarmEntity.getModifiedAt()))
            .build();
    }

}
