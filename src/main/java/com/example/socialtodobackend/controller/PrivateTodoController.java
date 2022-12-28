package com.example.socialtodobackend.controller;

import com.example.socialtodobackend.dto.PrivateTodoDto;
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
        return privateTodoService.createPrivateTodo(privateTodoDto);
    }


    @GetMapping("/private/todolist/{id}")
    public List<PrivateTodoDto> getPrivateTodoList(
        @PathVariable Long id
    ) {
        return privateTodoService.getAllPrivateTodo(id);
    }


    @PutMapping("/update/privatetodo")
    public List<PrivateTodoDto> updatePrivateTodo(
        @RequestBody PrivateTodoDto privateTodoDto
    ) {
        return privateTodoService.updatePrivateTodo(privateTodoDto);
    }


    @DeleteMapping("/delete/privatetodo")
    public List<PrivateTodoDto> removePrivateTodo(
        @RequestBody PrivateTodoDto privateTodoDto
    ) {
        return privateTodoService.deletePrivateTodo(privateTodoDto);
    }

}
















