package com.example.socialtodobackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FollowDto {
    @NotNull
    private Long followSentUserPKId;

    @NotNull
    private Long followReceivedUserPKId;

}