package com.example.socialtodobackend.dto.user;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UserSignInRequestDto {

    @NotNull
    @NotBlank
    private String email;

    @NotNull
    @NotBlank
    private String password;
}
