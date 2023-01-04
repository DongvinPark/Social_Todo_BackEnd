package com.example.socialtodobackend.dto;

import com.example.socialtodobackend.entity.PrivateTodoEntity;
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
public class PrivateTodoDto {

    private Long id;

    private Long authorUserId;

    private String todoContent;

    private String deadlineDate;

    private boolean isFinished;

    private String createdAt;

    private String modifiedAt;


    public static PrivateTodoDto fromEntity(PrivateTodoEntity entity){
        return PrivateTodoDto.builder()
            .id(entity.getId())
            .authorUserId(entity.getAuthorUserId())
            .todoContent(entity.getTodoContent())
            .deadlineDate(CommonUtils.dateToString(entity.getDeadlineDate()))
            .isFinished(entity.isFinished())
            .createdAt(CommonUtils.dateToString(entity.getCreatedAt()))
            .modifiedAt(CommonUtils.dateToString(entity.getModifiedAt()))
            .build();
    }

}
















