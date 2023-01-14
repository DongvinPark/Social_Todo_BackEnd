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
import com.example.socialtodobackend.persist.PublicTodoEntity;
import com.example.socialtodobackend.persist.PublicTodoRepository;
import com.example.socialtodobackend.persist.SupportEntity;
import com.example.socialtodobackend.persist.SupportRepository;
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
class SupportServiceTest {

    @Mock
    private PublicTodoRepository publicTodoRepository;

    @Mock
    private SupportRepository supportRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SupportService supportService;




    @Test
    @DisplayName("특정 공개 투두 아이템에 응원 1개 추가")
    void success_AddNag(){
        //given
        PublicTodoEntity publicTodoEntity = PublicTodoEntity.builder()
            .id(1L)
            .numberOfSupport(0L)
            .build();

        SupportEntity supportEntity = SupportEntity.builder()
            .supportSentUserPKId(2L)
            .publishedTodoPKId(1L)
            .build();

        given(publicTodoRepository.findById(anyLong())).willReturn(Optional.of(publicTodoEntity));

        given(publicTodoRepository.save(any())).willReturn(publicTodoEntity);

        given(supportRepository.save(any())).willReturn(supportEntity);

        //when
        supportService.addSupport(2L, 1L);

        //then
        verify(publicTodoRepository, times(1)).findById(anyLong());
        verify(publicTodoRepository, times(1)).save(any());
        verify(supportRepository, times(1)).save(any());
    }




    @Test
    @DisplayName("특정 공개 투두 아이템에 응원 1개 추가 실패 - 대상 투두 아이템 없음)")
    void failed_AddSupport_PublicTodoNotFound(){
        //given
        given(publicTodoRepository.findById(anyLong())).willReturn(Optional.empty());

        //when
        SocialTodoException exception = assertThrows(
            SocialTodoException.class ,
            () -> supportService.addSupport(2L, 1L)
        );

        //then
        assertEquals(ErrorCode.PUBLIC_TODO_NOT_FOUND, exception.getErrorCode());
    }




    @Test
    @DisplayName("특정 공개 투두 아이템에 응원 한 것 취소")
    void success_CancelSupport(){
        //given
        PublicTodoEntity publicTodoEntity = PublicTodoEntity.builder()
            .id(1L)
            .numberOfSupport(1L)
            .build();

        given(publicTodoRepository.findById(anyLong())).willReturn(Optional.of(publicTodoEntity));

        given(publicTodoRepository.save(any())).willReturn(publicTodoEntity);

        doNothing().when(supportRepository).deleteByPublishedTodoPKIdAndSupportSentUserPKId(anyLong(), anyLong());

        //when
        supportService.undoSupport(2L, 1L);

        //then
        verify(publicTodoRepository, times(1)).findById(anyLong());
        verify(publicTodoRepository, times(1)).save(any());
        verify(supportRepository, times(1)).deleteByPublishedTodoPKIdAndSupportSentUserPKId(anyLong(), anyLong());
    }




    @Test
    @DisplayName("특정 공개 투두 아이템에 응원 해준 유저들 확인 요청")
    void success_GetAllSupportSentUsers(){
        //given
        SupportEntity supportEntity = SupportEntity.builder()
            .supportSentUserPKId(2L)
            .publishedTodoPKId(1L)
            .build();

        UserEntity userEntity = UserEntity.builder()
            .id(2L)
            .nickname("two")
            .emailAddr("e2@main.com")
            .password("1111")
            .build();

        List<SupportEntity> supportEntityList = new ArrayList<>();
        supportEntityList.add(supportEntity);
        Slice<SupportEntity> nagSlice = new PageImpl<>(supportEntityList);

        List<UserEntity> followEntityList = new ArrayList<>();
        followEntityList.add(userEntity);
        Slice<UserEntity> userSlice = new PageImpl<>(followEntityList);

        given(
            supportRepository.findAllByPublishedTodoPKId(1L, PageRequest.of(0, CommonUtils.PAGE_SIZE))
        ).willReturn(nagSlice);

        given(
            userRepository.findAllByIdIn(
                nagSlice.getContent().stream().map(SupportEntity::getSupportSentUserPKId).collect(
                    Collectors.toList()),
                PageRequest.of(0, CommonUtils.PAGE_SIZE)
            )
        ).willReturn(userSlice);

        //when
        List<UserDto> userInfoDtoList = supportService.getAllSupportSentUsers(1L, PageRequest.of(0, CommonUtils.PAGE_SIZE));

        //then
        assertEquals(1, userInfoDtoList.size());
    }

}