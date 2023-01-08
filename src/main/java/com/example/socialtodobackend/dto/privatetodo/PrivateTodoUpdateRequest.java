package com.example.socialtodobackend.dto.privatetodo;

import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
public class PrivateTodoUpdateRequest {
    @NotNull
    private Long id;

    @NotNull
    private Long authorUserId;

    @NotNull
    private String todoContent;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate deadlineDate;

    @NotNull
    private boolean finished;
}
