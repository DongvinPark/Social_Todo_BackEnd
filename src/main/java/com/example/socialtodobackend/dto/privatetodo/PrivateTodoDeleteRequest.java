package com.example.socialtodobackend.dto.privatetodo;

import lombok.Getter;

@Getter
public class PrivateTodoDeleteRequest {
    private Long id;
    private Long authorUserPKId;
}
