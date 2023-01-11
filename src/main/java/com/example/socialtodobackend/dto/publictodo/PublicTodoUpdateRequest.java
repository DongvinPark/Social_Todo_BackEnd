package com.example.socialtodobackend.dto.publictodo;

import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
public class PublicTodoUpdateRequest {
    @NotNull
    private Long publicTodoPKId;

    @NotNull
    private boolean finished;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate deadlineDate;
}
