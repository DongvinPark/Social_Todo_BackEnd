package com.example.socialtodobackend.dto.privatetodo;

import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
public class PrivateTodoCreateRequest {

    @NotNull
    private String todoContent;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate deadlineDate;
}
