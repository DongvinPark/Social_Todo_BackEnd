package com.example.socialtodobackend.entity;

import com.example.socialtodobackend.type.AlarmTypeCode;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class AlarmEntity {

    @Id
    @GeneratedValue
    private Long id;

    private Long alarmReceiverUserId;
    private Long alarmSenderUserId;

    private Long numberOfPeopleRelatedToAlarm;

    private String alarmContent;

    private Long relatedPublicTodoPKId;

    @Enumerated(EnumType.STRING)
    private AlarmTypeCode alarmType;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

}















