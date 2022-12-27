package com.example.socialtodobackend.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.example.socialtodobackend.dto.PrivateTodoDto;
import com.example.socialtodobackend.service.PrivateTodoService;
import com.example.socialtodobackend.utils.CommonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
        List<PrivateTodoDto> privateTodoDtoList = Arrays.asList(
            PrivateTodoDto.builder()
                .id(1L)
                .authorUserId(1L)
                .todoContent("work")
                .deadlineDate("2022-12-31")
                .createdAt(CommonUtils.dateToString(LocalDateTime.now()))
                .modifiedAt(CommonUtils.dateToString(LocalDateTime.now()))
                .build()
        );

        given(privateTodoService.createPrivateTodo(
            PrivateTodoDto.builder()
                .authorUserId(1L)
                .todoContent("work")
                .deadlineDate("2022-12-31")
                .build()
        )).willReturn(
            privateTodoDtoList
        );
        //when

        //then
        mockMvc.perform(post("/create/privatetodo"))
            .andDo(print())
            .andExpect(jsonPath("$[0].authorUserId").value(1L))
            .andExpect(jsonPath("$[0].todoContent").value("work"))
            .andExpect(jsonPath("$[0].deadlineDate").value("2022-12-31"));
    }



    @Test
    void successfulDeletePrivateTodo(){
        //given

        //when

        //then

    }



    @Test
    void successfulGetPrivateTodoList(){
        //given

        //when

        //then

    }



    @Test
    void successfulUpdatePrivateTodo(){
        //given

        //when

        //then

    }




}



















