package com.example.socialtodobackend.dto.privatetodo;

import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PrivateTodoDeleteRequest {
    @NotNull
    private Long id;
}
