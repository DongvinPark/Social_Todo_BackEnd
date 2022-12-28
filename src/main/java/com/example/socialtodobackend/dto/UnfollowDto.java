package com.example.socialtodobackend.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UnfollowDto {

    private Long id;

    private Long followCanceledUserPKId;

    private Long followReceivedUserPKId;

}
