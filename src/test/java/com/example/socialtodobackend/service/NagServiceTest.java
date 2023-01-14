package com.example.socialtodobackend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.socialtodobackend.dto.user.UserDto;
import com.example.socialtodobackend.exception.SocialTodoException;
import com.example.socialtodobackend.persist.NagEntity;
import com.example.socialtodobackend.persist.NagRepository;
import com.example.socialtodobackend.persist.PublicTodoEntity;
import com.example.socialtodobackend.persist.PublicTodoRepository;
import com.example.socialtodobackend.persist.UserEntity;
import com.example.socialtodobackend.persist.UserRepository;
import com.example.socialtodobackend.type.ErrorCode;
import com.example.socialtodobackend.utils.CommonUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
class NagServiceTest {

    @Mock
    private PublicTodoRepository publicTodoRepository;

    @Mock
    private NagRepository nagRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NagService nagService;




    @Test
    @DisplayName("특정 공개 투두 아이템에 잔소리 1개 추가")
    void success_AddNag(){
        //given
        PublicTodoEntity publicTodoEntity = PublicTodoEntity.builder()
            .id(1L)
            .numberOfNag(0L)
            .build();

        NagEntity nagEntity = NagEntity.builder()
            .nagSentUserPKId(2L)
            .publishedTodoPKId(1L)
            .build();

        given(publicTodoRepository.findById(anyLong())).willReturn(Optional.of(publicTodoEntity));

        given(publicTodoRepository.save(any())).willReturn(publicTodoEntity);

        given(nagRepository.save(any())).willReturn(nagEntity);

        //when
        nagService.addNag(2L, 1L);

        //then
        verify(publicTodoRepository, times(1)).findById(anyLong());
        verify(publicTodoRepository, times(1)).save(any());
        verify(nagRepository, times(1)).save(any());
    }




    @Test
    @DisplayName("특정 공개 투두 아이템에 잔소리 1개 추가 실패 - 대상 투두 아이템 없음)")
    void failed_AddNag_PublicTodoNotFound(){
        //given
        given(publicTodoRepository.findById(anyLong())).willReturn(Optional.empty());

        //when
        SocialTodoException exception = assertThrows(
            SocialTodoException.class ,
            () -> nagService.addNag(2L, 1L)
        );

        //then
        assertEquals(ErrorCode.PUBLIC_TODO_NOT_FOUND, exception.getErrorCode());
    }




    @Test
    @DisplayName("특정 공개 투두 아이템에 잔소리 한 것 취소")
    void success_CancelNag(){
        //given
        PublicTodoEntity publicTodoEntity = PublicTodoEntity.builder()
            .id(1L)
            .numberOfNag(1L)
            .build();

        given(publicTodoRepository.findById(anyLong())).willReturn(Optional.of(publicTodoEntity));

        given(publicTodoRepository.save(any())).willReturn(publicTodoEntity);

        doNothing().when(nagRepository).deleteByPublishedTodoPKIdAndNagSentUserPKId(anyLong(), anyLong());

        //when
        nagService.undoNag(2L, 1L);

        //then
        verify(publicTodoRepository, times(1)).findById(anyLong());
        verify(publicTodoRepository, times(1)).save(any());
        verify(nagRepository, times(1)).deleteByPublishedTodoPKIdAndNagSentUserPKId(anyLong(), anyLong());
    }




    @Test
    @DisplayName("특정 공개 투두 아이템에 잔소리 해준 유저들 확인 요청")
    void success_GetAllNagSentUsers(){
        //given
        NagEntity nagEntity = NagEntity.builder()
            .nagSentUserPKId(2L)
            .publishedTodoPKId(1L)
            .build();

        UserEntity userEntity = UserEntity.builder()
            .id(2L)
            .nickname("two")
            .emailAddr("e2@main.com")
            .password("1111")
            .build();

        List<NagEntity> nagEntityList = new ArrayList<>();
        nagEntityList.add(nagEntity);
        Slice<NagEntity> nagSlice = new PageImpl<>(nagEntityList);

        List<UserEntity> followEntityList = new ArrayList<>();
        followEntityList.add(userEntity);
        Slice<UserEntity> userSlice = new PageImpl<>(followEntityList);

        given(
            nagRepository.findAllByPublishedTodoPKId(1L, PageRequest.of(0, CommonUtils.PAGE_SIZE))
        ).willReturn(nagSlice);

        given(
            userRepository.findAllByIdIn(
                nagSlice.getContent().stream().map(NagEntity::getNagSentUserPKId).collect(Collectors.toList()),
                PageRequest.of(0, CommonUtils.PAGE_SIZE)
            )
        ).willReturn(userSlice);

        //when
        List<UserDto> userFollowInfoDtoList = nagService.getAllNagSentUsers(1L, PageRequest.of(0, CommonUtils.PAGE_SIZE));

        //then
        assertEquals(1, userFollowInfoDtoList.size());
    }



}






























