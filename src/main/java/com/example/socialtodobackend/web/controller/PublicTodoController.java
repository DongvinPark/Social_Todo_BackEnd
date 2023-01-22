package com.example.socialtodobackend.web.controller;

import com.example.socialtodobackend.dto.APIDataResponse;
import com.example.socialtodobackend.dto.publictodo.PublicTodoCreateRequest;
import com.example.socialtodobackend.dto.publictodo.PublicTodoDto;
import com.example.socialtodobackend.dto.publictodo.PublicTodoUpdateRequest;
import com.example.socialtodobackend.service.PublicTodoService;
import com.example.socialtodobackend.utils.CommonUtils;
import io.swagger.annotations.ApiOperation;
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
    @ApiOperation("공개 투두 아이템 1개 생성")
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
    @ApiOperation("공개 투두 아이템 리스트 요청")
    public APIDataResponse< List<PublicTodoDto> > getAllPublicTodoList(
        @AuthenticationPrincipal Long authorUserPKId, @RequestParam int pageNumber
    ){
        /**
         * 레디스 서버를 사용해야 함을 기억하라!!
         * */
        PageRequest pageRequest = PageRequest.of(pageNumber, CommonUtils.PAGE_SIZE);
        return APIDataResponse.of(
            publicTodoService.getAllPublicTodo(authorUserPKId, pageRequest)
        );
    }




    @PutMapping("/update/public/todo")
    @ApiOperation("공개 투두 아이템 1개 수정")
    public void updatePublicTodo(
        @AuthenticationPrincipal Long authorUserPKId,
        @RequestBody @Valid PublicTodoUpdateRequest publicTodoDto
    ){
        publicTodoService.updatePublicTodo(authorUserPKId, publicTodoDto);
    }




    @DeleteMapping("/public/todo")
    @ApiOperation("공개 투두 아이템 1개 삭제")
    public void deletePublicTodo(
        @AuthenticationPrincipal Long authorUserPKId,
        @RequestParam Long publicTodoPKId
    ){
        /**
         * 응원/잔소리 해준 사람들 리포지토리에서 해당 투두 주키 아이디로 기록된 모든 엔티티들을 삭제해줘야 한다.
         * 이 때도 카프카를 이용해서 공개 투두 딜리트 이벤트를 프로듀스 한다.
         * 공개 투두 딜리트 이벤트 컨슈머 쪽에서는 publicTodoService.removePublicTodo(authorUserPKId, publicTodoPKId);
         * 메서드에서 하는 일을 그대로 다 하면 된다.
         * */
        publicTodoService.removePublicTodo(authorUserPKId, publicTodoPKId);
    }

}
