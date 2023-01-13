package com.example.socialtodobackend.controller;

import com.example.socialtodobackend.dto.APIDataResponse;
import com.example.socialtodobackend.dto.publictodo.PublicTodoCreateRequest;
import com.example.socialtodobackend.dto.publictodo.PublicTodoDto;
import com.example.socialtodobackend.dto.publictodo.PublicTodoUpdateRequest;
import com.example.socialtodobackend.service.PublicTodoService;
import com.example.socialtodobackend.utils.CommonUtils;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PublicTodoController {

    private final PublicTodoService publicTodoService;



    @PostMapping("/create/public/todo")
    public APIDataResponse< List<PublicTodoDto> > createPublicTodo(
        @AuthenticationPrincipal Long authorUserPKId,
        @RequestBody @Valid PublicTodoCreateRequest publicTodoCreateRequest
    ){
        publicTodoService.addPublicTodo(authorUserPKId, publicTodoCreateRequest);

        PageRequest pageRequest = PageRequest.of(0, CommonUtils.PAGE_SIZE);

        return APIDataResponse.of(
            publicTodoService.getAllPublicTodo(authorUserPKId, pageRequest)
        );
    }




    @GetMapping("/public/todo")
    public APIDataResponse< List<PublicTodoDto> > getAllPublicTodoList(
        @AuthenticationPrincipal Long authorUserPKId, @RequestParam int pageNumber
    ){
        PageRequest pageRequest = PageRequest.of(pageNumber, CommonUtils.PAGE_SIZE);
        return APIDataResponse.of(
            publicTodoService.getAllPublicTodo(authorUserPKId, pageRequest)
        );
    }




    @PutMapping("/update/public/todo")
    public void updatePublicTodo(
        @AuthenticationPrincipal Long authorUserPKId,
        @RequestBody @Valid PublicTodoUpdateRequest publicTodoDto
    ){
        publicTodoService.updatePublicTodo(authorUserPKId, publicTodoDto);
    }




    @DeleteMapping("/public/todo")
    public void deletePublicTodo(
        @AuthenticationPrincipal Long authorUserPKId,
        @RequestParam Long publicTodoPKId
    ){
        publicTodoService.removePublicTodo(authorUserPKId, publicTodoPKId);
    }

}
