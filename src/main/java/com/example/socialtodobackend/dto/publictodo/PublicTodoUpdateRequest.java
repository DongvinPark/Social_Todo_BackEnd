package com.example.socialtodobackend.dto.publictodo;

import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PublicTodoUpdateRequest {
    @NotNull
    private Long publicTodoPKId;

    @NotNull
    private Long authorUserPKId;

    @NotNull
    private boolean finished;

    @NotNull
    private String deadlineDate;
}
