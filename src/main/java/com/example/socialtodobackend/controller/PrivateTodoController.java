package com.example.socialtodobackend.controller;

import com.example.socialtodobackend.dto.PrivateTodoDto;
import com.example.socialtodobackend.entity.PrivateTodoEntity;
import com.example.socialtodobackend.service.PrivateTodoService;
import java.util.List;
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


    @PostMapping("/create/privatetodo")
    public List<PrivateTodoDto> addPrivateTodo(
        @RequestBody PrivateTodoDto privateTodoDto
    ) {
        PrivateTodoEntity createdPrivateTodoEntity = privateTodoService.createPrivateTodoEntity(
            privateTodoDto.getAuthorUserId(),
            privateTodoDto.getTodoContent(),
            privateTodoDto.getDeadlineDate()
            );
        return privateTodoService.getAllPrivateTodo(
            createdPrivateTodoEntity.getAuthorUserId()
        );
    }


    @GetMapping("/private/todolist/{authorUserPKId}")
    public List<PrivateTodoDto> getPrivateTodoList(
        @PathVariable Long authorUserPKId
    ) {
        return privateTodoService.getAllPrivateTodo(authorUserPKId);
    }


    @PutMapping("/update/privatetodo")
    public List<PrivateTodoDto> updatePrivateTodo(
        @RequestBody PrivateTodoDto privateTodoDto
    ) {
        PrivateTodoEntity updatedPrivateTodoEntity = privateTodoService.updatePrivateTodoEntity(privateTodoDto);
        return privateTodoService.getAllPrivateTodo(updatedPrivateTodoEntity.getAuthorUserId());
    }


    @DeleteMapping("/delete/privatetodo")
    public List<PrivateTodoDto> removePrivateTodo(
        @RequestBody PrivateTodoDto privateTodoDto
    ) {
        privateTodoService.deletePrivateTodo(privateTodoDto);
        return privateTodoService.getAllPrivateTodo(privateTodoDto.getAuthorUserId());
    }

}
















