package com.example.socialtodobackend.dto.alarm;

import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class AlarmDeleteRequest {
    @NotNull
    private Long alarmEntityPKId;
}
