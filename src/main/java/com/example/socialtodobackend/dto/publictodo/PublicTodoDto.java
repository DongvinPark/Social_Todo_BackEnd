package com.example.socialtodobackend.dto.publictodo;

import com.example.socialtodobackend.entity.PublicTodoEntity;
import java.time.LocalDate;
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
public class PublicTodoDto {

    private Long publicTodoPKId;

    private Long authorUserPKId;

    private String authorUserNickname;

    private String publicTodoContent;

    private boolean isFinished;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    private LocalDate deadlineDate;

    private Long numberOfSupport;

    private Long numberOfNag;

    public static PublicTodoDto fromEntity(PublicTodoEntity publicTodoEntity){
        return PublicTodoDto.builder()
            .publicTodoPKId(publicTodoEntity.getId())
            .authorUserPKId(publicTodoEntity.getAuthorUserId())
            .authorUserNickname(publicTodoEntity.getAuthorNickname())
            .publicTodoContent(publicTodoEntity.getTodoContent())
            .createdAt(publicTodoEntity.getCreatedAt())
            .modifiedAt(publicTodoEntity.getModifiedAt())
            .deadlineDate(publicTodoEntity.getDeadlineDate())
            .isFinished(publicTodoEntity.isFinished())
            .numberOfSupport(publicTodoEntity.getNumberOfSupport())
            .numberOfNag(publicTodoEntity.getNumberOfNag())
            .build();
    }

}
