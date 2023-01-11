package com.example.socialtodobackend.service;

import com.example.socialtodobackend.dto.privatetodo.PrivateTodoCreateRequest;
import com.example.socialtodobackend.dto.privatetodo.PrivateTodoDto;
import com.example.socialtodobackend.dto.privatetodo.PrivateTodoUpdateRequest;
import com.example.socialtodobackend.entity.PrivateTodoEntity;
import com.example.socialtodobackend.exception.SingletonException;
import com.example.socialtodobackend.repository.PrivateTodoRepository;
import com.example.socialtodobackend.utils.CommonUtils;
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
     * 프라이빗 투두 아이템 한 개를 추가한다.
     * */
    @Transactional
    public boolean createPrivateTodoEntity(
        Long authorUserPKId,
        PrivateTodoCreateRequest privateTodoCreateRequest
    ) {
        CommonUtils.validateContentLengthAndDeadlineDate(privateTodoCreateRequest.getTodoContent(), privateTodoCreateRequest.getDeadlineDate());

        privateTodoRepository.save(
            PrivateTodoEntity.builder()
                .authorUserId(authorUserPKId)
                .todoContent(privateTodoCreateRequest.getTodoContent())
                .isFinished(false)
                .deadlineDate(privateTodoCreateRequest.getDeadlineDate())
                .build()
        );

        return true;
    }



    /**
     * 특정한 사용자가 작성한 모든 프라이빗 투두 아이템 리스트를 반환한다.
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
    public void updatePrivateTodoEntity(Long userPKId, PrivateTodoUpdateRequest privateTodoUpdateRequest) {

        CommonUtils.validateContentLengthAndDeadlineDate(privateTodoUpdateRequest.getTodoContent(), privateTodoUpdateRequest.getDeadlineDate());

        PrivateTodoEntity privateTodoEntity = privateTodoRepository.findByIdAndAuthorUserId(privateTodoUpdateRequest.getId(), userPKId).orElseThrow( () -> SingletonException.PRIVATE_TODO_NOT_FOUND );

        privateTodoEntity.setTodoContent(privateTodoUpdateRequest.getTodoContent());
        privateTodoEntity.setFinished(privateTodoUpdateRequest.isFinished());
        privateTodoEntity.setDeadlineDate(privateTodoUpdateRequest.getDeadlineDate());

        privateTodoRepository.save(privateTodoEntity);
    }


    /**
     * 프라이빗 투두를 삭제한다.
     * */
    @Transactional
    public void deletePrivateTodo(Long userPKId, Long privateTodoPKId) {
        PrivateTodoEntity privateTodoEntity = privateTodoRepository.findByIdAndAuthorUserId(privateTodoPKId, userPKId).orElseThrow(
            () -> SingletonException.PRIVATE_TODO_NOT_FOUND
        );
        privateTodoRepository.deleteById(privateTodoEntity.getId());
    }

}




















