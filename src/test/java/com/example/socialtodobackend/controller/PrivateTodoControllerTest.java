package com.example.socialtodobackend.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.socialtodobackend.dto.privatetodo.PrivateTodoDto;
import com.example.socialtodobackend.persist.PrivateTodoEntity;
import com.example.socialtodobackend.service.PrivateTodoService;
import com.example.socialtodobackend.utils.CommonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PrivateTodoController.class)
class PrivateTodoControllerTest {

    @MockBean
    private PrivateTodoService privateTodoService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;



    @Test
    void successfulCreatePrivateTodo() throws Exception {
        //given
        List<PrivateTodoDto> privateTodoDtoList = new ArrayList<>();
        privateTodoDtoList.add(
            (
                PrivateTodoDto.builder()
                    .id(1L)
                    .authorUserId(1L)
                    .todoContent("work")
                    .deadlineDate("2023-12-31")
                    .createdAt(CommonUtils.dateToString(LocalDateTime.now()))
                    .modifiedAt(CommonUtils.dateToString(LocalDateTime.now()))
                    .isFinished(false)
                    .build()
            )
        );

        given(privateTodoService.createPrivateTodoEntity(1L, "work", "2023-12-31"))
            .willReturn(
                PrivateTodoEntity.builder()
                    .authorUserId(1L)
                    .todoContent("work")
                    .isFinished(false)
                    .deadlineDate(CommonUtils.stringToDate("2023-12-31"))
                    .build()
            );
        given(privateTodoService.getAllPrivateTodo(1L)).willReturn(privateTodoDtoList);

        //when
        //then
        mockMvc.perform(
            post("/create/privatetodo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new PrivateTodoDto(1L, 1L, "work", "2023-12-31", false, CommonUtils.dateToString(LocalDateTime.now()), CommonUtils.dateToString(LocalDateTime.now())
                ))
            )
            )
            .andDo(print())
            .andExpect(jsonPath("$[0]").exists())
            .andExpect(jsonPath("$[0].authorUserId").value(1L))
            .andExpect(jsonPath("$[0].todoContent").value("work"))
            .andExpect(jsonPath("$[0].deadlineDate").value("2023-12-31"));
    }





    @Test
    void successfulDeletePrivateTodo() throws Exception {
        List<PrivateTodoDto> privateTodoDtoList = new ArrayList<>();
        privateTodoDtoList.add(
            (
                PrivateTodoDto.builder()
                    .id(1L)
                    .authorUserId(1L)
                    .todoContent("work")
                    .deadlineDate("2023-12-31")
                    .createdAt(CommonUtils.dateToString(LocalDateTime.now()))
                    .modifiedAt(CommonUtils.dateToString(LocalDateTime.now()))
                    .isFinished(false)
                    .build()
            )
        );
        List<PrivateTodoDto> afterDeletedList = new ArrayList<>();

        given(privateTodoService.deletePrivateTodo(
            PrivateTodoDto.builder()
                .id(1L)
                .build()
        ))
            .willReturn(true);

        //when
        //then
        mockMvc.perform(
                delete("/delete/privatetodo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(
                        new PrivateTodoDto(1L, 1L, "work", "2023-12-31", false, CommonUtils.dateToString(LocalDateTime.now()), CommonUtils.dateToString(LocalDateTime.now())
                        ))
                    )
            )
            .andDo(print())
            .andExpect(jsonPath("$.length()").value(0));
    }






    @Test
    void successfulGetPrivateTodoList() throws Exception {
        //given
        List<PrivateTodoDto> privateTodoDtoList = new ArrayList<>();
        privateTodoDtoList.add(
            PrivateTodoDto.builder()
                .id(1L)
                .authorUserId(1L)
                .todoContent("work")
                .deadlineDate("2023-12-31")
                .createdAt(CommonUtils.dateToString(LocalDateTime.now()))
                .modifiedAt(CommonUtils.dateToString(LocalDateTime.now()))
                .isFinished(false)
                .build()
        );

        given(privateTodoService.getAllPrivateTodo(1L)).willReturn(privateTodoDtoList);

        //when
        //then
        mockMvc.perform(get("/private/todolist/1"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0]").exists())
            .andExpect(jsonPath("$[0].authorUserId").value(1L))
            .andExpect(jsonPath("$[0].todoContent").value("work"))
            .andExpect(jsonPath("$[0].deadlineDate").value("2023-12-31"));

    }




    /**
     * public List<PrivateTodoDto> updatePrivateTodo(
     * @RequestBody PrivateTodoDto privateTodoDto
     * ){...}
     * 메서드는 privateTodoService.update()와 privateTodoService.getAllPrivateTodo() 로 이루어져 있으므로,
     * 두 개의 메서드를 privateTodoService 클래스 테스트에서 증명하는 것으로 대신한다.
     * */




}



















