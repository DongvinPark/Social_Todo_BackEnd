package com.example.socialtodobackend.controller;

import com.example.socialtodobackend.dto.APIDataResponse;
import com.example.socialtodobackend.dto.privatetodo.PrivateTodoCreateRequest;
import com.example.socialtodobackend.dto.privatetodo.PrivateTodoDto;
import com.example.socialtodobackend.dto.privatetodo.PrivateTodoUpdateRequest;
import com.example.socialtodobackend.service.PrivateTodoService;
import com.example.socialtodobackend.utils.CommonUtils;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
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
public class PrivateTodoController {

    private final PrivateTodoService privateTodoService;


    @PostMapping("/create/private/todo")
    public APIDataResponse< List<PrivateTodoDto> > addPrivateTodo(
        @AuthenticationPrincipal Long authorUserPKId,
        @RequestBody @Valid PrivateTodoCreateRequest privateTodoCreateRequest
    ) {
        privateTodoService.createPrivateTodoEntity(authorUserPKId, privateTodoCreateRequest);

        PageRequest pageRequest = PageRequest.of(0, CommonUtils.PAGE_SIZE);
        return APIDataResponse.of(
            privateTodoService.getAllPrivateTodo(authorUserPKId, pageRequest)
        );
    }


    @GetMapping("/private/todos")
    public APIDataResponse<List<PrivateTodoDto>> getPrivateTodoList(
        @AuthenticationPrincipal Long authorUserPKId,
        @RequestParam int pageNumber
    ) {
        PageRequest pageRequest = PageRequest.of(pageNumber, CommonUtils.PAGE_SIZE);
        return APIDataResponse.of(
            privateTodoService.getAllPrivateTodo(authorUserPKId, pageRequest)
        );
    }


    @PutMapping("/update/private/todo")
    public void updatePrivateTodo(
        @AuthenticationPrincipal Long userPKId,
        @RequestBody @Valid PrivateTodoUpdateRequest privateTodoUpdateRequest
    ) {
        privateTodoService.updatePrivateTodoEntity(userPKId, privateTodoUpdateRequest);
    }


    @DeleteMapping("/private/todo")
    public void removePrivateTodo(
        @AuthenticationPrincipal Long userPKId,
        @RequestParam Long privateTodoPKId
    ) {
        privateTodoService.deletePrivateTodo(userPKId, privateTodoPKId);
    }

}
















