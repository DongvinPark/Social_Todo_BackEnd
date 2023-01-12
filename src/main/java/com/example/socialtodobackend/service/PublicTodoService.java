package com.example.socialtodobackend.service;

import com.example.socialtodobackend.dto.publictodo.PublicTodoCreateRequest;
import com.example.socialtodobackend.dto.publictodo.PublicTodoDto;
import com.example.socialtodobackend.dto.publictodo.PublicTodoUpdateRequest;
import com.example.socialtodobackend.persist.PublicTodoEntity;
import com.example.socialtodobackend.exception.SingletonException;
import com.example.socialtodobackend.persist.PublicTodoRepository;
import com.example.socialtodobackend.persist.UserRepository;
import com.example.socialtodobackend.utils.CommonUtils;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicTodoService {

    private final PublicTodoRepository publicTodoRepository;
    private final UserRepository userRepository;



    /**
     * 특정 유저가 작성한 모든 공개 투두 아이템들을 읽어들인다.
     * 이때, 공개 투두 아이템 각각에 대한 응원/잔소리 정보도 같이 줘야 한다.
     * */
    @Transactional(readOnly = true)
    public List<PublicTodoDto> getAllPublicTodo(Long authorUserPKId, PageRequest pageRequest) {
        if(!userRepository.existsById(authorUserPKId)){
            throw SingletonException.USER_NOT_FOUND;
        }

        return publicTodoRepository.findAllByAuthorUserId(authorUserPKId, pageRequest).getContent().stream().map(PublicTodoDto::fromEntity).collect(Collectors.toList());
    }




    /**
     * 공개 투두 아이템을 추가한다.
     * */
    @Transactional
    public void addPublicTodo(Long authorUserPKId, PublicTodoCreateRequest publicTodoCreateRequest) {
        if(!userRepository.existsById(authorUserPKId)){
            throw SingletonException.USER_NOT_FOUND;
        }
        CommonUtils.validateContentLengthAndDeadlineDate(publicTodoCreateRequest.getPublicTodoContent(), publicTodoCreateRequest.getDeadlineDate());

        publicTodoRepository.save(
            PublicTodoEntity.builder()
                .authorUserId(authorUserPKId)
                .authorNickname(publicTodoCreateRequest.getAuthorUserNickname())
                .todoContent(publicTodoCreateRequest.getPublicTodoContent())
                .deadlineDate(publicTodoCreateRequest.getDeadlineDate())
                .finished(false)
                .numberOfSupport(0L)
                .numberOfNag(0L)
                .build()
        );
    }




    /**
     * 공개 투두 아이템을 수정한다.
     * 한 번 공개한 공개 투투 아이템의 content는 수정이 불가능하다.
     * 디데이 기한 또는 완료 여부만을 수정할 수 있다.
     * 한 번 완료 처리한 공개 투두 아이템은 어떤 수정도 불가능하며 삭제만이 가능하다.
     * */
    @Transactional
    public void updatePublicTodo(Long authorUserPKId, PublicTodoUpdateRequest publicTodoUpdateRequest) {
        PublicTodoEntity publicTodoEntity = publicTodoRepository.findByIdAndAuthorUserId(publicTodoUpdateRequest.getPublicTodoPKId(), authorUserPKId).orElseThrow(
            () -> SingletonException.PUBLIC_TODO_NOT_FOUND
        );

        if(publicTodoEntity.isFinished()){
            throw SingletonException.CANNOT_UPDATE_FINISHED_PUBLIC_TODO_ITEM;
        }

        CommonUtils.validateContentLengthAndDeadlineDate(null, publicTodoUpdateRequest.getDeadlineDate());

        publicTodoEntity.setDeadlineDate(publicTodoUpdateRequest.getDeadlineDate());
        publicTodoEntity.setFinished(publicTodoUpdateRequest.isFinished());
        publicTodoRepository.save(publicTodoEntity);
    }





    /**
     * 공개 투두 아이템을 삭제한다.
     * */
    @Transactional
    public void removePublicTodo(Long authorUserPKId, Long publicTodoPKId) {
        publicTodoRepository.deleteByIdAndAuthorUserId(publicTodoPKId, authorUserPKId);
    }

}
















