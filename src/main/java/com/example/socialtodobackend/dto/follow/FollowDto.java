package com.example.socialtodobackend.dto.follow;

import javax.validation.constraints.NotNull;
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
public class FollowDto {

    @NotNull
    private Long followRelationTargetUserPKId;

}
