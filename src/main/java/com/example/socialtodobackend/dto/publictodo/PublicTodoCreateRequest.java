package com.example.socialtodobackend.dto.publictodo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PublicTodoCreateRequest {
    @NotNull
    private Long authorUserPKId;

    @NotNull
    private String authorUserNickname;

    @NotNull
    @NotBlank
    private String publicTodoContent;

    @NotNull
    @NotBlank
    private String deadlineDate;
}
