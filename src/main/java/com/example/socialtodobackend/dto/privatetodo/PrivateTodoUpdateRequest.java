package com.example.socialtodobackend.dto.privatetodo;

import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PrivateTodoUpdateRequest {
    @NotNull
    private Long id;

    @NotNull
    private Long authorUserId;

    @NotNull
    private String todoContent;

    @NotNull
    private String deadlineDate;

    @NotNull
    private boolean finished;
}
