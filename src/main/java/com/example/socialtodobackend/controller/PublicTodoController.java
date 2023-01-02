package com.example.socialtodobackend.controller;

import com.example.socialtodobackend.dto.PublicTodoDto;
import com.example.socialtodobackend.service.AlarmService;
import com.example.socialtodobackend.service.PublicTodoService;
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
public class PublicTodoController {

    private final PublicTodoService publicTodoService;
    private final AlarmService alarmService;


    @GetMapping("/get/publictodo/{authorUserPKId}")
    public List<PublicTodoDto> getAllPublicList(
        @PathVariable Long authorUserPKId
    ){
        return publicTodoService.getAllPublicTodo(authorUserPKId);
    }




    @PostMapping("/create/publictodo")
    public List<PublicTodoDto> createPublicTodo(
        @RequestBody PublicTodoDto publicTodoDto
    ){
        publicTodoService.addPublicTodo(publicTodoDto);
        return publicTodoService.getAllPublicTodo(publicTodoDto.getAuthorUserPKId());
    }




    @PutMapping("/update/publictodo")
    public List<PublicTodoDto> updatePublicTodo(
        @RequestBody PublicTodoDto publicTodoDto
    ){
        boolean result = publicTodoService.updatePublicTodo(publicTodoDto);

        //만약 수정에 성공했는데, 그 수정의 내용이 완료처리였다면 알림을 보내야 한다.
        if(result && publicTodoDto.isFinished()){
            alarmService.sendPublicTodoFinishInfoAlarm();
        }

        return publicTodoService.getAllPublicTodo(publicTodoDto.getAuthorUserPKId());
    }




    @DeleteMapping("/delete/publictodo/{publicTodoPKId}")
    public List<PublicTodoDto> deletePublicTodo(
        @RequestBody PublicTodoDto publicTodoDto
    ){
        //디데이가 오늘인데, 아직도 완료처리되지 못한 공개 투투 아이템들은 타임라인 캐싱의 대상이므로 수정이 금지 된다.
        //그 외의 경우에는 삭제 가능하다.
        publicTodoService.removePublicTodo(publicTodoDto.getPublicTodoPKId());
        return publicTodoService.getAllPublicTodo(publicTodoDto.getAuthorUserPKId());
    }

}
