package com.example.socialtodobackend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.socialtodobackend.dto.follow.UserFollowInfoDto;
import com.example.socialtodobackend.exception.SocialTodoException;
import com.example.socialtodobackend.persist.FollowEntity;
import com.example.socialtodobackend.persist.FollowRepository;
import com.example.socialtodobackend.persist.UserEntity;
import com.example.socialtodobackend.persist.UserRepository;
import com.example.socialtodobackend.type.ErrorCode;
import com.example.socialtodobackend.utils.CommonUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {

    @Mock
    private FollowRepository followRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FollowService followService;




    @Test
    @DisplayName("특정 유저를 팔로우 하고 있는 다른 유저들 리스트 요청")
    void success_getFollowers(){
        //given
        UserEntity userEntity = UserEntity.builder()
            .id(1L)
            .emailAddr("e1@mail.com")
            .password("1111")
            .nickname("one")
            .build();

        FollowEntity followEntity = FollowEntity.builder()
            .followSentUserId(2L)
            .followReceivedUserId(1L)
            .build();

        List<UserEntity> followerList = new ArrayList<>();
        followerList.add(userEntity);
        Slice<UserEntity> userSlice = new PageImpl<>(followerList);

        List<FollowEntity> followEntityList = new ArrayList<>();
        followEntityList.add(followEntity);
        Slice<FollowEntity> followSlice = new PageImpl<>(followEntityList);

        given(
            followRepository.findAllByFollowReceivedUserId(1L, PageRequest.of(0, CommonUtils.PAGE_SIZE))
        ).willReturn(followSlice);

        given(
            userRepository.findAllByIdIn(
                followSlice.getContent().stream().map(FollowEntity::getFollowSentUserId).collect(
                Collectors.toList()),
                PageRequest.of(0, CommonUtils.PAGE_SIZE)
            )
        ).willReturn(userSlice);

        //when
        List<UserFollowInfoDto> userFollowInfoDtoList = followService.getFollowers(1L, PageRequest.of(0, CommonUtils.PAGE_SIZE));

        //then
        assertEquals(1, userFollowInfoDtoList.size());
    }




    @Test
    @DisplayName("특정 유저가 팔로우를 하고 있는 다른 모든 유저들 요청")
    void success_getFollowees(){
        //given
        UserEntity userEntity = UserEntity.builder()
            .id(1L)
            .emailAddr("e2@mail.com")
            .password("1111")
            .nickname("two")
            .build();

        FollowEntity followEntity = FollowEntity.builder()
            .followSentUserId(1L)
            .followReceivedUserId(2L)
            .build();

        List<UserEntity> followeeList = new ArrayList<>();
        followeeList.add(userEntity);
        Slice<UserEntity> userSlice = new PageImpl<>(followeeList);

        List<FollowEntity> followEntityList = new ArrayList<>();
        followEntityList.add(followEntity);
        Slice<FollowEntity> followSlice = new PageImpl<>(followEntityList);

        given(
            followRepository.findAllByFollowSentUserId(1L, PageRequest.of(0, CommonUtils.PAGE_SIZE))
        ).willReturn(followSlice);

        given(
            userRepository.findAllByIdIn(
                followSlice.getContent().stream().map(FollowEntity::getFollowReceivedUserId).collect(
                    Collectors.toList()),
                PageRequest.of(0, CommonUtils.PAGE_SIZE)
            )
        ).willReturn(userSlice);

        //when
        List<UserFollowInfoDto> userFollowInfoDtoList = followService.getFollowees(1L, PageRequest.of(0, CommonUtils.PAGE_SIZE));

        //then
        assertEquals(1, userFollowInfoDtoList.size());
    }




    @Test
    @DisplayName("팔로우 관계 추가 요청")
    void success_addFollowInfo(){
        //given
        FollowEntity followEntity = FollowEntity.builder()
            .followSentUserId(2L)
            .followReceivedUserId(1L)
            .build();

        given(userRepository.existsById(1L)).willReturn(true);

        given(followRepository.save(any())).willReturn(followEntity);

        //when
        followService.addFollowInfo(2L, 1L);

        //then
        verify(followRepository, times(1)).save(any());
    }




    @Test
    @DisplayName("팔로우 관계 추가 실패 - 대상 유저 없음")
    void failed_addFollowInfo_UserNotFound(){
        //given
        given(userRepository.existsById(anyLong())).willReturn(false);

        //when
        SocialTodoException exception = assertThrows(
            SocialTodoException.class ,
            () -> followService.addFollowInfo(2L, 1L)
        );

        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }




    @Test
    @DisplayName("팔로우 관계 추가 실패 - 5000명 초과하여 팔로우 함")
    void failed_addFollowInfo_FollowLimitExceed(){
        //given
        given(userRepository.existsById(anyLong())).willReturn(true);

        given(followRepository.countAllByFollowSentUserId(anyLong())).willReturn(CommonUtils.FOLLOW_LIMIT);

        //when
        SocialTodoException exception = assertThrows(
            SocialTodoException.class ,
            () -> followService.addFollowInfo(2L, 1L)
        );

        //then
        assertEquals(ErrorCode.CANNOT_FOLLOW_MORE_THAN_5000_USERS, exception.getErrorCode());
    }




    @Test
    @DisplayName("팔로우 정보 삭제 요청 == 언팔로우")
    void success_deleteFollowInfo(){
        //given
        doNothing().when(followRepository).deleteByFollowSentUserIdEqualsAndFollowReceivedUserIdEquals(anyLong(), anyLong());

        //when
        followService.deleteFollowInfo(1L, 2L);

        //then
        verify(followRepository, times(1)).deleteByFollowSentUserIdEqualsAndFollowReceivedUserIdEquals(1L, 2L);
    }



}






