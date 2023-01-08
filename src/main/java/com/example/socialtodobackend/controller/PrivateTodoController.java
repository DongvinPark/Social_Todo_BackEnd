package com.example.socialtodobackend.controller;

import com.example.socialtodobackend.dto.APIDataResponse;
import com.example.socialtodobackend.dto.privatetodo.PrivateTodoCreateRequest;
import com.example.socialtodobackend.dto.privatetodo.PrivateTodoDeleteRequest;
import com.example.socialtodobackend.dto.privatetodo.PrivateTodoDto;
import com.example.socialtodobackend.dto.privatetodo.PrivateTodoUpdateRequest;
import com.example.socialtodobackend.service.PrivateTodoService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PrivateTodoController {

    private final PrivateTodoService privateTodoService;


    @PostMapping("/create/private/todo")
    public APIDataResponse< List<PrivateTodoDto> > addPrivateTodo(
        @RequestBody @Valid PrivateTodoCreateRequest privateTodoCreateRequest
    ) {
        privateTodoService.createPrivateTodoEntity(privateTodoCreateRequest);
        return APIDataResponse.of(
            privateTodoService.getAllPrivateTodo(privateTodoCreateRequest.getAuthorUserId())
        );
    }


    @GetMapping("/private/todos/{authorUserPKId}")
    public APIDataResponse< List<PrivateTodoDto> > getPrivateTodoList(
        @PathVariable Long authorUserPKId
    ) {
        return APIDataResponse.of(
            privateTodoService.getAllPrivateTodo(authorUserPKId)
        );
    }


    @PutMapping("/update/private/todo")
    public void updatePrivateTodo(
        @RequestBody @Valid PrivateTodoUpdateRequest privateTodoUpdateRequest
    ) {
        privateTodoService.updatePrivateTodoEntity(privateTodoUpdateRequest);
    }


    @DeleteMapping("/delete/private/todo")
    public void removePrivateTodo(
        @RequestBody @Valid PrivateTodoDeleteRequest privateTodoDeleteRequest
    ) {
        privateTodoService.deletePrivateTodo(privateTodoDeleteRequest);
    }

}
















