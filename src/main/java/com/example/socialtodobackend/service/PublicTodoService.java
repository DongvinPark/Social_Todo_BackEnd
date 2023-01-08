package com.example.socialtodobackend.service;

import com.example.socialtodobackend.dto.publictodo.PublicTodoCreateRequest;
import com.example.socialtodobackend.dto.publictodo.PublicTodoDto;
import com.example.socialtodobackend.dto.publictodo.PublicTodoUpdateRequest;
import com.example.socialtodobackend.entity.PublicTodoEntity;
import com.example.socialtodobackend.exception.SocialTodoException;
import com.example.socialtodobackend.repository.PublicTodoRepository;
import com.example.socialtodobackend.repository.UserRepository;
import com.example.socialtodobackend.type.ErrorCode;
import java.time.LocalDate;
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
            throw new SocialTodoException(ErrorCode.USER_NOT_FOUND);
        }

        return publicTodoRepository.findAllByAuthorUserId(authorUserPKId, pageRequest).getContent().stream().map(PublicTodoDto::fromEntity).collect(Collectors.toList());
    }




    /**
     * 공개 투두 아이템을 추가한다.
     * <br><br/>
     * 공개 투두 아이템이 추가되는 즉시, followSendCountRepository에 해당 공개 투두 아이템의
     * 주키와 동일한 주키를 가지는 엔티티를 저장해야 한다.
     * */
    @Transactional
    public boolean addPublicTodo(PublicTodoCreateRequest publicTodoCreateRequest) {
        if(!userRepository.findById(publicTodoCreateRequest.getAuthorUserPKId()).isPresent()){
            throw new SocialTodoException(ErrorCode.USER_NOT_FOUND);
        }

        validateContentLength(publicTodoCreateRequest.getPublicTodoContent());
        validateDeadlineDate(publicTodoCreateRequest.getDeadlineDate());

        publicTodoRepository.save(
            PublicTodoEntity.builder()
                .authorUserId(publicTodoCreateRequest.getAuthorUserPKId())
                .authorNickname(publicTodoCreateRequest.getAuthorUserNickname())
                .todoContent(publicTodoCreateRequest.getPublicTodoContent())
                .deadlineDate(publicTodoCreateRequest.getDeadlineDate())
                .finished(false)
                .numberOfSupport(0L)
                .numberOfNag(0L)
                .build()
        );

        return true;
    }






    /**
     * 공개 투두 아이템을 수정한다.
     * 한 번 공개한 공개 투투 아이템의 content는 수정이 불가능하다. 상황에 따라서 디데이 기한 또는 완료 여부를 수정할 수 있을 뿐이다.
     * 한 번 완료 처리한 공개 투두 아이템은 어떤 수정도 불가능하며 삭제만이 가능하다.
     * <br><br/>
     * 아직 완료 처리가 되지 않은 것을 전제했을 때, 수정이 가능한 경우는 오직 세 가지 뿐이고 각각의 경우에 대해서도 수정할 수 있는 범위가 정해져 있다.
     * <br><br/>
     * 오늘 마감 기한에 도달했다면, 완료 처리하는 것만 가능하다.
     * <br><br/>
     * 마감 기한이 도달하지 않았거나 이미 지나버렸다면, 마감기한과 완료 여부를 변경시킬 수 있다.
     * */
    @Transactional
    public boolean updatePublicTodo(PublicTodoUpdateRequest publicTodoUpdateRequest) {
        PublicTodoEntity publicTodoEntity = publicTodoRepository.findById(publicTodoUpdateRequest.getPublicTodoPKId()).orElseThrow(
            () -> new SocialTodoException(ErrorCode.PUBLIC_TODO_NOT_FOUND)
        );

        if(publicTodoEntity.isFinished()){
            throw new SocialTodoException(ErrorCode.CANNOT_UPDATE_FINISHED_PUBLIC_TODO_ITEM);
        }

        //찾아낸 원본 엔티티의 마감 기한이 오늘인 경우를 처리한다. 이때는 오직 '완료처리'만을 할 수 있다.
        if(
            LocalDate.now()
                .equals(publicTodoEntity.getDeadlineDate())
        ){
            publicTodoEntity.setFinished(publicTodoUpdateRequest.isFinished());
            publicTodoRepository.save(publicTodoEntity);
            log.info("마감기한 도달한 날에 할일 완료처리");
        } else {
            validateDeadlineDate(publicTodoUpdateRequest.getDeadlineDate());

            publicTodoEntity.setDeadlineDate(publicTodoUpdateRequest.getDeadlineDate());
            publicTodoEntity.setFinished(publicTodoUpdateRequest.isFinished());
            publicTodoRepository.save(publicTodoEntity);
        }
        return true;
    }





    /**
     * 공개 투두 아이템을 삭제한다.
     * <br><br/>
     * 오늘 마감기한에 도달했는데, 아직 완료 처라가 되지 않은 경우 타임랴인 캐싱의 대상이 되므로 삭제가 불가능하다.
     * 그 이외에는 삭제가 가능하다.
     * */
    @Transactional
    public boolean removePublicTodo(Long publicTodoPKId) {

        PublicTodoEntity publicTodoEntity = publicTodoRepository.findById(publicTodoPKId).orElseThrow(
            () -> new SocialTodoException(ErrorCode.PUBLIC_TODO_NOT_FOUND)
        );

        if(
            LocalDate.now()
                .equals(publicTodoEntity.getDeadlineDate())
            &&
                !publicTodoEntity.isFinished()
        ){
            throw new SocialTodoException(ErrorCode.CANNOT_DELETE_TIMELINE_TARGET_TODO_ITEM);
        }

        publicTodoRepository.deleteById(publicTodoPKId);

        return true;
    }




    //------------ PRIVATE HELPER METHOD AREA ----------




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
















