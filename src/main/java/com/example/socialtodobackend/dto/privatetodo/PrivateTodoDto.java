package com.example.socialtodobackend.dto.privatetodo;

import com.example.socialtodobackend.persist.PrivateTodoEntity;
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
public class PrivateTodoDto {

    private Long id;

    private Long authorUserId;

    private String todoContent;

    private LocalDate deadlineDate;

    private boolean isFinished;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;


    public static PrivateTodoDto fromEntity(PrivateTodoEntity entity){
        return PrivateTodoDto.builder()
            .id(entity.getId())
            .authorUserId(entity.getAuthorUserId())
            .todoContent(entity.getTodoContent())
            .deadlineDate(entity.getDeadlineDate())
            .isFinished(entity.isFinished())
            .createdAt(entity.getCreatedAt())
            .modifiedAt(entity.getModifiedAt())
            .build();
    }

}
















