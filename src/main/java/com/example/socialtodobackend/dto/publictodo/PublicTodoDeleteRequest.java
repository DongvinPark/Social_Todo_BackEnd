package com.example.socialtodobackend.dto.publictodo;

import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PublicTodoDeleteRequest {
    @NotNull
    private Long publicTodoPKId;

    @NotNull
    private Long authorUserPKId;
}
