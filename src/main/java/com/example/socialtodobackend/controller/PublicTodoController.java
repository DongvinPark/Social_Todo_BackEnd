package com.example.socialtodobackend.controller;

import com.example.socialtodobackend.dto.APIDataResponse;
import com.example.socialtodobackend.dto.publictodo.PublicTodoCreateRequest;
import com.example.socialtodobackend.dto.publictodo.PublicTodoDeleteRequest;
import com.example.socialtodobackend.dto.publictodo.PublicTodoDto;
import com.example.socialtodobackend.dto.publictodo.PublicTodoUpdateRequest;
import com.example.socialtodobackend.service.AlarmService;
import com.example.socialtodobackend.service.PublicTodoService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PublicTodoController {

    private final PublicTodoService publicTodoService;
    private final AlarmService alarmService;



    @PostMapping("/create/public/todo")
    public APIDataResponse< List<PublicTodoDto> > createPublicTodo(
        @RequestBody @Valid PublicTodoCreateRequest publicTodoCreateRequest
    ){
        publicTodoService.addPublicTodo(publicTodoCreateRequest);
        return APIDataResponse.of(
            publicTodoService.getAllPublicTodo(publicTodoCreateRequest.getAuthorUserPKId())
        );
    }




    @GetMapping("/public/todo/{authorUserPKId}")
    public APIDataResponse< List<PublicTodoDto> > getAllPublicTodoList(
        @PathVariable Long authorUserPKId
    ){
        return APIDataResponse.of(
            publicTodoService.getAllPublicTodo(authorUserPKId)
        );
    }




    @PutMapping("/update/public/todo")
    public void updatePublicTodo(
        @RequestBody @Valid PublicTodoUpdateRequest publicTodoDto
    ){
        publicTodoService.updatePublicTodo(publicTodoDto);
    }




    @DeleteMapping("/delete/public/todo")
    public void deletePublicTodo(
        @RequestBody @Valid PublicTodoDeleteRequest publicTodoDeleteRequest
    ){
        //디데이가 오늘인데, 아직도 완료처리되지 못한 공개 투투 아이템들은 타임라인 캐싱의 대상이므로 수정이 금지 된다.
        //그 외의 경우에는 삭제 가능하다.
        publicTodoService.removePublicTodo(publicTodoDeleteRequest.getPublicTodoPKId());
    }

}
