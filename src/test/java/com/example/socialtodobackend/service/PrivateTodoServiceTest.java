package com.example.socialtodobackend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.socialtodobackend.dto.privatetodo.PrivateTodoCreateRequest;
import com.example.socialtodobackend.dto.privatetodo.PrivateTodoDto;
import com.example.socialtodobackend.dto.privatetodo.PrivateTodoUpdateRequest;
import com.example.socialtodobackend.exception.SocialTodoException;
import com.example.socialtodobackend.persist.PrivateTodoEntity;
import com.example.socialtodobackend.persist.PrivateTodoRepository;
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
class PrivateTodoServiceTest {

    @Mock
    private PrivateTodoRepository privateTodoRepository;

    @InjectMocks
    private PrivateTodoService privateTodoService;




    @Test
    @DisplayName("프라이빗 투두 아이템 1개 추가 요청")
    void success_CreatePrivateTodoEntity (){
        //given
        PrivateTodoEntity privateTodoEntity = PrivateTodoEntity.builder()
            .id(1L)
            .todoContent("work")
            .isFinished(false)
            .authorUserId(1L)
            .deadlineDate(LocalDate.of(2023,12,31))
            .build();

        PrivateTodoCreateRequest privateTodoCreateRequest = PrivateTodoCreateRequest.builder()
            .todoContent("work")
            .deadlineDate(LocalDate.of(2023,12,31))
            .build();

        given(privateTodoRepository.save(any())).willReturn(privateTodoEntity);

        //when
        privateTodoService.createPrivateTodoEntity(1L, privateTodoCreateRequest);

        //then
        verify(privateTodoRepository, times(1)).save(any());
    }




    @Test
    @DisplayName("프라이빗 투두 아이템 1개 추가 요청 실패 - 할일 내용이 빈 문자열")
    void failed_CreatePrivateTodo_ContentLengthZero(){
        //given
        PrivateTodoCreateRequest privateTodoCreateRequest = PrivateTodoCreateRequest.builder()
            .todoContent("")
            .deadlineDate(LocalDate.of(2023,12,31))
            .build();

        //when
        SocialTodoException exception = assertThrows(
            SocialTodoException.class ,
            () -> privateTodoService.createPrivateTodoEntity(1L, privateTodoCreateRequest)
        );

        //then
        assertEquals(ErrorCode.ZERO_CONTENT_LENGTH, exception.getErrorCode());
    }




    @Test
    @DisplayName("프라이빗 투두 아이템 1개 추가 요청 실패 - 할일 내용이 한계 길이 초과")
    void failed_CreatePrivateTodo_ContentTooLong(){
        //given
        String tooLongString = "000000000_000000000_000000000_000000000_000000000_000000000_000000000_000000000_000000000_000000000_*";

        PrivateTodoCreateRequest privateTodoCreateRequest = PrivateTodoCreateRequest.builder()
            .todoContent(tooLongString)
            .deadlineDate(LocalDate.of(2023,12,31))
            .build();

        //when
        SocialTodoException exception = assertThrows(
            SocialTodoException.class ,
            () -> privateTodoService.createPrivateTodoEntity(1L, privateTodoCreateRequest)
        );

        //then
        assertEquals(ErrorCode.CONTENT_LENGTH_TOO_LONG, exception.getErrorCode());
    }




    @Test
    @DisplayName("프라이빗 투두 아이템 1개 추가 요청 실패 - 마감기한 과거로 설정함")
    void failed_CreatePrivateTodo_SetDeadlineDatePast(){
        //given
        PrivateTodoCreateRequest privateTodoCreateRequest = PrivateTodoCreateRequest.builder()
            .todoContent("work")
            .deadlineDate(LocalDate.of(2021,12,31))
            .build();

        //when
        SocialTodoException exception = assertThrows(
            SocialTodoException.class ,
            () -> privateTodoService.createPrivateTodoEntity(1L, privateTodoCreateRequest)
        );

        //then
        assertEquals(ErrorCode.CANNOT_SET_TODO_DEADLINE_ON_PAST, exception.getErrorCode());
    }




    @Test
    @DisplayName("프라이빗 투두 아이템 1개 추가 요청 실패 - 마감 기한 1년 초과")
    void failed_CreatePrivateTodo_DeadlineDateTooFar(){
        //given
        PrivateTodoCreateRequest privateTodoCreateRequest = PrivateTodoCreateRequest.builder()
            .todoContent("work")
            .deadlineDate(LocalDate.now().plusDays(366))
            .build();

        //when
        SocialTodoException exception = assertThrows(
            SocialTodoException.class ,
            () -> privateTodoService.createPrivateTodoEntity(1L, privateTodoCreateRequest)
        );

        //then
        assertEquals(ErrorCode.CANNOT_SET_TODO_DEADLINE_AFTER_365DAYS, exception.getErrorCode());
    }




    @Test
    @DisplayName("모든 프라이빗 투두 아이템 요청")
    void success_GetAllPrivateTodo(){
        //given
        PrivateTodoEntity privateTodoEntity = PrivateTodoEntity.builder()
            .id(1L)
            .todoContent("work")
            .deadlineDate(LocalDate.of(2023,12,31))
            .authorUserId(1L)
            .build();

        List<PrivateTodoEntity> privateTodoEntityList = new ArrayList<>();
        privateTodoEntityList.add(privateTodoEntity);

        Slice<PrivateTodoEntity> slice = new PageImpl<>(privateTodoEntityList);

        given(privateTodoRepository.findAllByAuthorUserId(
            1L, PageRequest.of(0, CommonUtils.PAGE_SIZE)
        )).willReturn(slice);

        //when
        List<PrivateTodoDto> privateTodoDtoList = privateTodoService.getAllPrivateTodo(1L, PageRequest.of(0, CommonUtils.PAGE_SIZE));

        //then
        assertEquals(1, privateTodoDtoList.size());
    }




    @Test
    @DisplayName("프라이빗 투두 아이템 1개 수정 요청")
    void success_UpdatePrivateTodoEntity(){
        //given
        PrivateTodoEntity privateTodoEntity = PrivateTodoEntity.builder()
            .id(1L)
            .todoContent("work")
            .deadlineDate(LocalDate.of(2023,12,31))
            .authorUserId(1L)
            .build();

        PrivateTodoUpdateRequest privateTodoUpdateRequest = PrivateTodoUpdateRequest.builder()
            .id(1L)
            .todoContent("work2")
            .deadlineDate(LocalDate.of(2023,12,30))
            .finished(true)
            .build();

        given(privateTodoRepository.findByIdAndAuthorUserId(anyLong(), anyLong())).willReturn(
            Optional.of(privateTodoEntity));

        given(privateTodoRepository.save(any())).willReturn(privateTodoEntity);

        //when
        privateTodoService.updatePrivateTodoEntity(1L, privateTodoUpdateRequest);

        //then
        verify(privateTodoRepository, times(1)).findByIdAndAuthorUserId(anyLong(), anyLong());
        verify(privateTodoRepository, times(1)).save(any());
    }




    @Test
    @DisplayName("프라이빗 투두 아이템 1개 삭제 요청")
    void success_DeletePrivateTodo(){
        //given
        doNothing().when(privateTodoRepository).deleteByIdAndAuthorUserId(anyLong(), anyLong());

        //when
        privateTodoService.deletePrivateTodo(1L, 1L);

        //then
        verify(privateTodoRepository, times(1)).deleteByIdAndAuthorUserId(1L, 1L);
    }





}

















