package com.example.socialtodobackend.dto.user;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserSignUpRequestDto {

    @NotBlank
    @NotNull
    private String nickname;

    @NotBlank
    @NotNull
    private String password;

    @NotBlank
    @NotNull
    private String emailAddr;

}
