package com.example.socialtodobackend.controller;

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
    public List<PrivateTodoDto> addPrivateTodo(
        @RequestBody @Valid PrivateTodoCreateRequest privateTodoCreateRequest
    ) {
        privateTodoService.createPrivateTodoEntity(privateTodoCreateRequest);
        return privateTodoService.getAllPrivateTodo(privateTodoCreateRequest.getAuthorUserId());
    }


    @GetMapping("/get/private/todos/{authorUserPKId}")
    public List<PrivateTodoDto> getPrivateTodoList(
        @PathVariable Long authorUserPKId
    ) {
        return privateTodoService.getAllPrivateTodo(authorUserPKId);
    }


    @PutMapping("/update/private/todo")
    public List<PrivateTodoDto> updatePrivateTodo(
        @RequestBody @Valid PrivateTodoUpdateRequest privateTodoUpdateRequest
    ) {
        privateTodoService.updatePrivateTodoEntity(privateTodoUpdateRequest);
        return privateTodoService.getAllPrivateTodo(privateTodoUpdateRequest.getAuthorUserId());
    }


    @DeleteMapping("/delete/private/todo")
    public List<PrivateTodoDto> removePrivateTodo(
        @RequestBody @Valid PrivateTodoDeleteRequest privateTodoDeleteRequest
    ) {
        privateTodoService.deletePrivateTodo(privateTodoDeleteRequest);
        return privateTodoService.getAllPrivateTodo(privateTodoDeleteRequest.getAuthorUserPKId());
    }

}
















