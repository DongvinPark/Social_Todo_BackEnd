package com.example.socialtodobackend.dto.privatetodo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PrivateTodoCreateRequest {
    @NotNull
    private Long authorUserId;

    @NotNull
    private String todoContent;

    @NotNull
    @NotBlank
    private String deadlineDate;
}
