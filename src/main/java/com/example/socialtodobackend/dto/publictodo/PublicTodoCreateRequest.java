package com.example.socialtodobackend.dto.publictodo;

import java.time.LocalDate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
public class PublicTodoCreateRequest {
    @NotNull
    private Long authorUserPKId;

    @NotNull
    private String authorUserNickname;

    @NotNull
    @NotBlank
    private String publicTodoContent;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate deadlineDate;
}
