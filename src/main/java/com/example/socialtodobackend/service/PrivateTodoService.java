package com.example.socialtodobackend.service;

import com.example.socialtodobackend.dto.privatetodo.PrivateTodoCreateRequest;
import com.example.socialtodobackend.dto.privatetodo.PrivateTodoDeleteRequest;
import com.example.socialtodobackend.dto.privatetodo.PrivateTodoDto;
import com.example.socialtodobackend.dto.privatetodo.PrivateTodoUpdateRequest;
import com.example.socialtodobackend.entity.PrivateTodoEntity;
import com.example.socialtodobackend.exception.SocialTodoException;
import com.example.socialtodobackend.repository.PrivateTodoRepository;
import com.example.socialtodobackend.type.ErrorCode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PrivateTodoService {

    private final PrivateTodoRepository privateTodoRepository;


    /**
     * 프라이빗 투두 아이템 한 개를 추가하고, 그 후의 프라이빗 투두 리스트를 반환한다.
     * */
    @Transactional
    public boolean createPrivateTodoEntity(
        PrivateTodoCreateRequest privateTodoCreateRequest
    ) {
        validateContentLength(privateTodoCreateRequest.getTodoContent());
        validateDeadlineDate(privateTodoCreateRequest.getDeadlineDate());

        privateTodoRepository.save(
            PrivateTodoEntity.builder()
                .authorUserId(privateTodoCreateRequest.getAuthorUserId())
                .todoContent(privateTodoCreateRequest.getTodoContent())
                .isFinished(false)
                .deadlineDate(privateTodoCreateRequest.getDeadlineDate())
                .build()
        );

        return true;
    }



    /**
     * 특정한 사용자가 작성한 모든 프라이빗 투두 아이템 리스트를 반환한다.
     * 인증 기능을 도입할 때, 내가 쓴 것만 확인할 수 있도록 조치해야 한다.
     * */
    @Transactional(readOnly = true)
    public List<PrivateTodoDto> getAllPrivateTodo(long authorUserPKId, PageRequest pageRequest) {
        return privateTodoRepository.findAllByAuthorUserId(authorUserPKId, pageRequest).getContent().stream().map(PrivateTodoDto::fromEntity).collect(
            Collectors.toList());
    }



    /**
     * 프라이빗 투두 아이템 1개를 수정한 후, 수정된 결과 리스트를 반환한다.
     * 프라이빗 투두 아이템에서 유저가 수정할 수 있는 것은 해당 투두 아이템의 내용뿐이다.
     * 또한 자기 자신의 프라이빗 투두만을 수정할 수 있다.
     * */
    @Transactional
    public boolean updatePrivateTodoEntity(PrivateTodoUpdateRequest privateTodoUpdateRequest) {

        validateContentLength(privateTodoUpdateRequest.getTodoContent());
        //validateDateFormat(privateTodoUpdateRequest.getDeadlineDate());
        validateDeadlineDate(privateTodoUpdateRequest.getDeadlineDate());

        PrivateTodoEntity privateTodoEntity = privateTodoRepository.findById(privateTodoUpdateRequest.getId()).orElseThrow( () -> new SocialTodoException(
            ErrorCode.PRIVATE_TODO_NOT_FOUND) );

        privateTodoEntity.setTodoContent(privateTodoUpdateRequest.getTodoContent());
        privateTodoEntity.setFinished(privateTodoUpdateRequest.isFinished());
        privateTodoEntity.setDeadlineDate(privateTodoUpdateRequest.getDeadlineDate());

        privateTodoRepository.save(privateTodoEntity);

        return true;
    }


    /**
     * 프라이빗 투두를 삭제하거나, 완료했을 때
     * 해당 투두 아이템을 삭제완료 하고 그 후의 프라이빗 투두 리스트를 반환한다.
     * JPA는 delete 할 때 비효율적인 측면이 있으므로
     * 추후에 JPA의 delete메서드를 거치지 않고 DB에 쿼리를 직접 날리는 방식으로 수정하는 것이 필요하다.
     * */
    @Transactional
    public boolean deletePrivateTodo(PrivateTodoDeleteRequest privateTodoDeleteRequest) {
        PrivateTodoEntity privateTodoEntity = privateTodoRepository.findById(privateTodoDeleteRequest.getId()).orElseThrow(
            () -> new SocialTodoException(ErrorCode.PRIVATE_TODO_NOT_FOUND)
        );
        privateTodoRepository.deleteById(privateTodoEntity.getId());

        return true;
    }



    //------------------ PRIVATE HELPER METHODS ------------

    /**
     * 투두 컨텐츠의 길이는 0자 이상 100자 이내여야 한다.
     * */
    private void validateContentLength(String privateTodoContent){
        if(privateTodoContent.length() == 0 || privateTodoContent == null){
            throw new SocialTodoException(ErrorCode.ZERO_CONTENT_LENGTH);
        }
        if(privateTodoContent.length() > 100){
            throw new SocialTodoException(ErrorCode.CONTENT_LENGTH_TOO_LONG);
        }
    }



    /**
     * 오늘 만들어진 투두 아이템의 데드라인 날짜는 가장 빠르게 설정할 경우 오늘로 설정할 수 있고,
     * 가장 늦게 설정할 경우, 오늘로부터 365일이 지난 날짜까지 설정할 수 있다.
     * 그 이외의 디데이 설정은 전부 무효처리 한다.
     * */
    private void validateDeadlineDate(LocalDate dateInput){
        if(dateInput.isBefore(LocalDate.now() ) ){
            throw new SocialTodoException(ErrorCode.CANNOT_SET_PRIVATE_TODO_DEADLINE_ON_PAST);
        }
        if(dateInput.isAfter( LocalDate.now().plusDays(365) )){
            throw new SocialTodoException(ErrorCode.CANNOT_SET_PRIVATE_TODO_DEADLINE_AFTER_365DAYS);
        }
    }

}




















