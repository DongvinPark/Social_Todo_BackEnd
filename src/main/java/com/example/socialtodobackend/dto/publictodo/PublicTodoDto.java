package com.example.socialtodobackend.dto;

import com.example.socialtodobackend.entity.PublicTodoEntity;
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
public class PublicTodoDto {

    private Long publicTodoPKId;

    private Long authorUserPKId;

    private String authorUserNickname;

    private String publicTodoContent;

    private boolean isFinished;

    private String createdAt;

    private String modifiedAt;

    private String deadlineDate;

    public static PublicTodoDto fromEntity(PublicTodoEntity publicTodoEntity){
        return PublicTodoDto.builder()
            .publicTodoPKId(publicTodoEntity.getId())
            .authorUserPKId(publicTodoEntity.getAuthorUserId())
            .authorUserNickname(publicTodoEntity.getAuthorNickname())
            .publicTodoContent(publicTodoEntity.getTodoContent())
            .createdAt(CommonUtils.dateToString(publicTodoEntity.getCreatedAt()))
            .modifiedAt(CommonUtils.dateToString(publicTodoEntity.getModifiedAt()))
            .deadlineDate(CommonUtils.dateToString(publicTodoEntity.getDeadlineDate()))
            .isFinished(publicTodoEntity.isFinished())
            .build();
    }

}
