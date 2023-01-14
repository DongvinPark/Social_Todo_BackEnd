package com.example.socialtodobackend.dto.publictodo;

import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@Builder
public class PublicTodoCreateRequest {

    @NotNull
    private String authorUserNickname;

    @NotNull
    private String publicTodoContent;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate deadlineDate;
}
