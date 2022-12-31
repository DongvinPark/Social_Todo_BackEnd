package com.example.socialtodobackend.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.socialtodobackend.dto.PrivateTodoDto;
import com.example.socialtodobackend.entity.PrivateTodoEntity;
import com.example.socialtodobackend.exception.SocialTodoException;
import com.example.socialtodobackend.repository.PrivateTodoRepository;
import com.example.socialtodobackend.type.ErrorCode;
import com.example.socialtodobackend.utils.CommonUtils;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class PrivateTodoServiceTest {

    @Mock
    private PrivateTodoRepository privateTodoRepository;

    @InjectMocks
    private PrivateTodoService privateTodoService;


    @Test
    void failedCreatePrivateTodo_ContentTooLong(){
        //given
        String tooLongContent = "000000000_000000000_000000000_000000000_000000000_000000000_000000000_000000000_000000000_000000000_000000000_";

        //when
        SocialTodoException exception = assertThrows(
            SocialTodoException.class ,
            () -> privateTodoService.createPrivateTodoEntity(1L, tooLongContent, "2023-12-31")
        );

        //then
        assertEquals(ErrorCode.CONTENT_LENGTH_TOO_LONG, exception.getErrorCode());
    }




    @Test
    void failedCreatePrivateTodo_DeadlineDateBeforeToday(){
        //given
        String pastDateString = "2022-12-30";

        //when
        SocialTodoException exception = assertThrows(
            SocialTodoException.class ,
            () -> privateTodoService.createPrivateTodoEntity(1L, "make test code", pastDateString)
        );

        //then
        assertEquals(ErrorCode.CANNOT_SET_PRIVATE_TODO_DEADLINE_ON_PAST, exception.getErrorCode());

    }




    @Test
    void failedCreatePrivateTodo_InvalidDeadLineDateFormat(){
        //given
        String invalidDeadlineDateString = "2023.12.30";

        //when
        SocialTodoException exception = assertThrows(
            SocialTodoException.class ,
            () -> privateTodoService.createPrivateTodoEntity(1L, "make test code", invalidDeadlineDateString)
        );

        //then
        assertEquals(ErrorCode.INVALID_DEADLINE_DATE_FORMAT, exception.getErrorCode());

    }




    /**
     * 예를 들어서 오늘이 1/1이라면, 최대로 설정 가능한 마감 기한 날짜는 1/1 로부터 365일이 지난 이후의 날짜이다. 이 날짜를 초과하면 마감 기한으로 설정할 수 없다.
     * */
    @Test
    void failedCreatePrivateTodo_DeadlineDateAfter366DaysFromToday(){
        //given
        String tooFarDeadlineDateString = CommonUtils.dateToString(LocalDateTime.now().plusDays(366));

        //when
        SocialTodoException exception = assertThrows(
            SocialTodoException.class ,
            () -> privateTodoService.createPrivateTodoEntity(1L, "make test code", tooFarDeadlineDateString)
        );

        //then
        assertEquals(ErrorCode.CANNOT_SET_PRIVATE_TODO_DEADLINE_AFTER_365DAYS, exception.getErrorCode());
    }




    @Test
    void failedUpdatePrivateTodo_PrivateTodoNotFound(){
        //given
        given(privateTodoRepository.findById(anyLong())).willReturn(Optional.empty());

        //when
        SocialTodoException exception = assertThrows(SocialTodoException.class, ()-> privateTodoService.updatePrivateTodoEntity(
            PrivateTodoDto.builder()
                .id(2L)
                .todoContent("work")
                .isFinished(false)
                .deadlineDate(CommonUtils.dateToString(LocalDateTime.now()))
                .build()
        ));

        //then
        assertEquals(ErrorCode.PRIVATE_TODO_NOT_FOUND, exception.getErrorCode());
    }





    @Test
    void failedUpdatePrivateTodo_InvalidDeadlineDateFormat(){
        //given
        String invalidDeadlineDateString = "2023-12-30-";

        //when
        SocialTodoException exception = assertThrows(
            SocialTodoException.class ,
            () -> privateTodoService.updatePrivateTodoEntity(
                PrivateTodoDto.builder()
                    .id(1L)
                    .authorUserId(1L)
                    .deadlineDate(invalidDeadlineDateString)
                    .todoContent("work")
                    .build()
            )
        );

        //then
        assertEquals(ErrorCode.INVALID_DEADLINE_DATE_FORMAT, exception.getErrorCode());

    }




    @Test
    void successCreatePrivateTodo(){
        //given
        PrivateTodoEntity privateTodoEntity = PrivateTodoEntity.builder()
            .id(1L)
            .authorUserId(1L)
            .todoContent("work")
            .isFinished(false)
            .deadlineDate(CommonUtils.stringToDate("2023-12-30"))
            .build();

        given(privateTodoRepository.save(any()))
            .willReturn(privateTodoEntity);

        ArgumentCaptor<PrivateTodoEntity> captor = ArgumentCaptor.forClass(PrivateTodoEntity.class);

        //when
        PrivateTodoEntity entity = privateTodoService.createPrivateTodoEntity(1L, "work", "2023-12-30");

        //then
        assertEquals(1L, entity.getAuthorUserId());
        verify(privateTodoRepository, times(1)).save(captor.capture());
        assertEquals("work", captor.getValue().getTodoContent());
        assertEquals("2023-12-30", CommonUtils.dateToString(captor.getValue().getDeadlineDate()));
        assertFalse(captor.getValue().isFinished());
    }




    @Test
    void successGetAllPrivateTodo(){
        //given
        PrivateTodoEntity privateTodoEntity = PrivateTodoEntity.builder()
            .id(1L)
            .authorUserId(1L)
            .todoContent("work")
            .isFinished(false)
            .createdAt(LocalDateTime.now())
            .modifiedAt(LocalDateTime.now())
            .deadlineDate(CommonUtils.stringToDate("2023-12-30"))
            .build();

        given(privateTodoRepository.findAllByAuthorUserId(anyLong()))
            .willReturn(
                Arrays.asList(
                    privateTodoEntity
                )
            );

        //when
        List<PrivateTodoDto> dtoList = privateTodoService.getAllPrivateTodo(1L);

        //then
        assertEquals(1, dtoList.size());
        assertEquals("work", dtoList.get(0).getTodoContent());
        assertEquals("2023-12-30", dtoList.get(0).getDeadlineDate());
        assertFalse(dtoList.get(0).isFinished());
    }




    @Test
    void successUpdatePrivateTodo(){
        //given
        PrivateTodoEntity privateTodoEntity = PrivateTodoEntity.builder()
            .id(1L)
            .authorUserId(1L)
            .todoContent("work")
            .isFinished(false)
            .deadlineDate(CommonUtils.stringToDate("2023-12-30"))
            .build();

        given(privateTodoRepository.save(any()))
            .willReturn(privateTodoEntity);

        given(privateTodoRepository.findById(1L)).willReturn(Optional.of(privateTodoEntity));

        ArgumentCaptor<PrivateTodoEntity> captor = ArgumentCaptor.forClass(PrivateTodoEntity.class);

        //when
        PrivateTodoEntity entity = privateTodoService.updatePrivateTodoEntity(
            PrivateTodoDto.builder()
                .id(1L)
                .todoContent("play game")
                .deadlineDate("2023-01-01")
                .isFinished(true)
                .build()
        );

        //then
        assertEquals(1L, entity.getAuthorUserId());
        verify(privateTodoRepository, times(1)).save(captor.capture());
        assertEquals("play game", captor.getValue().getTodoContent());
        assertEquals("2023-01-01", CommonUtils.dateToString(captor.getValue().getDeadlineDate()));
        assertTrue(captor.getValue().isFinished());
    }




    @Test
    void successDeletePrivateTodo(){
        //given
        PrivateTodoEntity privateTodoEntity = PrivateTodoEntity.builder()
            .id(1L)
            .authorUserId(1L)
            .todoContent("work")
            .isFinished(false)
            .deadlineDate(CommonUtils.stringToDate("2023-12-30"))
            .build();

        given(privateTodoRepository.findById(1L)).willReturn(Optional.of(privateTodoEntity));

        //when
        boolean result = privateTodoService.deletePrivateTodo(
            PrivateTodoDto.builder()
                .id(1L)
                .build()
        );

        //then
        assertTrue(result);
    }




}























