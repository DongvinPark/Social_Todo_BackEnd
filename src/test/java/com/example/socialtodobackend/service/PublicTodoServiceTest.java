package com.example.socialtodobackend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.socialtodobackend.dto.publictodo.PublicTodoCreateRequest;
import com.example.socialtodobackend.dto.publictodo.PublicTodoDto;
import com.example.socialtodobackend.dto.publictodo.PublicTodoUpdateRequest;
import com.example.socialtodobackend.exception.SocialTodoException;
import com.example.socialtodobackend.persist.PublicTodoEntity;
import com.example.socialtodobackend.persist.PublicTodoRepository;
import com.example.socialtodobackend.type.ErrorCode;
import com.example.socialtodobackend.utils.CommonUtils;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
class PublicTodoServiceTest {

    @Mock
    private PublicTodoRepository publicTodoRepository;

    @InjectMocks
    private PublicTodoService publicTodoService;




    @Test
    @DisplayName("모든 공개 투두 아이템 리스트 요청")
    void success_GetAllPublicTodo(){
        //given
        PublicTodoEntity publicTodoEntity = PublicTodoEntity.builder()
            .id(1L)
            .todoContent("work")
            .deadlineDate(LocalDate.of(2023,12,31))
            .authorUserId(1L)
            .build();

        List<PublicTodoEntity> publicTodoEntityList = new ArrayList<>();
        publicTodoEntityList.add(publicTodoEntity);

        Slice<PublicTodoEntity> slice = new PageImpl<>(publicTodoEntityList);

        given(publicTodoRepository.findAllByAuthorUserId(
            1L, PageRequest.of(0, CommonUtils.PAGE_SIZE)
        )).willReturn(slice);

        //when
        List<PublicTodoDto> publicTodoDtoList = publicTodoService.getAllPublicTodo(1L, PageRequest.of(0, CommonUtils.PAGE_SIZE));

        //then
        assertEquals(1, publicTodoDtoList.size());
    }




    @Test
    @DisplayName("공개 투두 아이템 1개 추가 요청")
    void success_CreatePublicTodoEntity (){
        //given
        PublicTodoEntity publicTodoEntity = PublicTodoEntity.builder()
            .id(1L)
            .todoContent("work")
            .finished(false)
            .authorUserId(1L)
            .deadlineDate(LocalDate.of(2023,12,31))
            .build();

        PublicTodoCreateRequest publicTodoCreateRequest = PublicTodoCreateRequest.builder()
            .authorUserNickname("one")
            .publicTodoContent("work")
            .deadlineDate(LocalDate.of(2023,12,31))
            .build();

        given(publicTodoRepository.save(any())).willReturn(publicTodoEntity);

        //when
        publicTodoService.addPublicTodo(1L, publicTodoCreateRequest);

        //then
        verify(publicTodoRepository, times(1)).save(any());
    }




    @Test
    @DisplayName("공개 투두 아이템 1개 추가 요청 실패 - 할일 내용이 빈 문자열")
    void failed_CreatePublicTodo_ContentLengthZero(){
        //given
        PublicTodoCreateRequest publicTodoCreateRequest = PublicTodoCreateRequest.builder()
            .authorUserNickname("one")
            .publicTodoContent("")
            .deadlineDate(LocalDate.of(2023,12,31))
            .build();

        //when
        SocialTodoException exception = assertThrows(
            SocialTodoException.class ,
            () -> publicTodoService.addPublicTodo(1L, publicTodoCreateRequest)
        );

        //then
        assertEquals(ErrorCode.ZERO_CONTENT_LENGTH, exception.getErrorCode());
    }




    @Test
    @DisplayName("공개 투두 아이템 1개 추가 요청 실패 - 할일 내용이 한계 길이 초과")
    void failed_CreatePublicTodo_ContentTooLong(){
        //given
        String tooLongString = "000000000_000000000_000000000_000000000_000000000_000000000_000000000_000000000_000000000_000000000_*";

        PublicTodoCreateRequest publicTodoCreateRequest = PublicTodoCreateRequest.builder()
            .authorUserNickname("one")
            .publicTodoContent(tooLongString)
            .deadlineDate(LocalDate.of(2023,12,31))
            .build();

        //when
        SocialTodoException exception = assertThrows(
            SocialTodoException.class ,
            () -> publicTodoService.addPublicTodo(1L, publicTodoCreateRequest)
        );

        //then
        assertEquals(ErrorCode.CONTENT_LENGTH_TOO_LONG, exception.getErrorCode());
    }




    @Test
    @DisplayName("공개 투두 아이템 1개 추가 요청 실패 - 마감기한 과거로 설정함")
    void failed_CreatePublicTodo_SetDeadlineDatePast(){
        //given
        PublicTodoCreateRequest publicTodoCreateRequest = PublicTodoCreateRequest.builder()
            .authorUserNickname("one")
            .publicTodoContent("work")
            .deadlineDate(LocalDate.of(2021,12,31))
            .build();

        //when
        SocialTodoException exception = assertThrows(
            SocialTodoException.class ,
            () -> publicTodoService.addPublicTodo(1L, publicTodoCreateRequest)
        );

        //then
        assertEquals(ErrorCode.CANNOT_SET_TODO_DEADLINE_ON_PAST, exception.getErrorCode());
    }




    @Test
    @DisplayName("공개 투두 아이템 1개 추가 요청 실패 - 마감 기한 1년 초과")
    void failed_CreatePublicTodo_DeadlineDateTooFar(){
        //given
        PublicTodoCreateRequest publicTodoCreateRequest = PublicTodoCreateRequest.builder()
            .authorUserNickname("one")
            .publicTodoContent("work")
            .deadlineDate(LocalDate.now().plusDays(366))
            .build();

        //when
        SocialTodoException exception = assertThrows(
            SocialTodoException.class ,
            () -> publicTodoService.addPublicTodo(1L, publicTodoCreateRequest)
        );

        //then
        assertEquals(ErrorCode.CANNOT_SET_TODO_DEADLINE_AFTER_365DAYS, exception.getErrorCode());
    }




    @Test
    @DisplayName("공개 투두 아이템 1개 수정 요청 실패 - 이미 마감 처리함")
    void failed_UpdatePublicTodo_AlreadyFinished(){
        //given
        PublicTodoEntity publicTodoEntity = PublicTodoEntity.builder()
            .id(1L)
            .todoContent("work")
            .deadlineDate(LocalDate.of(2023,12,31))
            .authorNickname("one")
            .finished(true)
            .build();

        PublicTodoUpdateRequest publicTodoUpdateRequest = PublicTodoUpdateRequest.builder()
            .publicTodoPKId(1L)
            .finished(false)
            .deadlineDate(LocalDate.now())
            .build();

        given(publicTodoRepository.findByIdAndAuthorUserId(anyLong(), any())).willReturn(Optional.of(publicTodoEntity));

        //when
        SocialTodoException exception = assertThrows(
            SocialTodoException.class ,
            () -> publicTodoService.updatePublicTodo(1L, publicTodoUpdateRequest)
        );

        //then
        assertEquals(ErrorCode.CANNOT_UPDATE_FINISHED_PUBLIC_TODO_ITEM, exception.getErrorCode());
    }




    @Test
    @DisplayName("공개 투두 아이템 1개 수정 요청")
    void success_UpdatePublicTodo_AlreadyFinished(){
        //given
        PublicTodoEntity publicTodoEntity = PublicTodoEntity.builder()
            .id(1L)
            .todoContent("work")
            .authorUserId(1L)
            .deadlineDate(LocalDate.of(2023,12,31))
            .authorNickname("one")
            .finished(false)
            .build();

        PublicTodoUpdateRequest publicTodoUpdateRequest = PublicTodoUpdateRequest.builder()
            .publicTodoPKId(1L)
            .finished(false)
            .deadlineDate(LocalDate.now())
            .build();

        given(publicTodoRepository.findByIdAndAuthorUserId(anyLong(), any())).willReturn(Optional.of(publicTodoEntity));

        given(publicTodoRepository.save(any())).willReturn(publicTodoEntity);

        //when
        publicTodoService.updatePublicTodo(1L, publicTodoUpdateRequest);

        //then
        verify(publicTodoRepository, times(1)).findByIdAndAuthorUserId(anyLong(), anyLong());
        verify(publicTodoRepository, times(1)).save(any());
    }




    @Test
    @DisplayName("공개 투두 아이템 1개 삭제 요청")
    void success_RemovePublicTodo(){
        //given
        doNothing().when(publicTodoRepository).deleteByIdAndAuthorUserId(anyLong(), anyLong());

        //when
        publicTodoService.removePublicTodo(1L, 1L);

        //then
        verify(publicTodoRepository, times(1)).deleteByIdAndAuthorUserId(1L, 1L);
    }


}