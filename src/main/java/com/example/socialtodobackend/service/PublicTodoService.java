package com.example.socialtodobackend.service;

import com.example.socialtodobackend.dto.publictodo.PublicTodoCreateRequest;
import com.example.socialtodobackend.dto.publictodo.PublicTodoDto;
import com.example.socialtodobackend.dto.publictodo.PublicTodoUpdateRequest;
import com.example.socialtodobackend.exception.SingletonException;
import com.example.socialtodobackend.persist.NagRepository;
import com.example.socialtodobackend.persist.PublicTodoEntity;
import com.example.socialtodobackend.persist.PublicTodoRepository;
import com.example.socialtodobackend.persist.SupportRepository;
import com.example.socialtodobackend.persist.redis.numbers.NagNumberCacheRepository;
import com.example.socialtodobackend.persist.redis.numbers.SupportNumberCacheRepository;
import com.example.socialtodobackend.utils.CommonUtils;
import java.util.ArrayList;
import java.util.List;
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
    private final SupportRepository supportRepository;
    private final NagRepository nagRepository;
    private final SupportNumberCacheRepository supportNumberCacheRepository;
    private final NagNumberCacheRepository nagNumberCacheRepository;



    /**
     * 특정 유저가 작성한 모든 공개 투두 아이템들을 읽어들인다.
     * 이때, 공개 투두 아이템 각각에 대한 응원/잔소리 정보를 레디스 캐시서버에서 가져와서 PublicTodoDto 구성에 사용해야 한다.
     * */
    @Transactional(readOnly = true)
    public List<PublicTodoDto> getAllPublicTodo(Long authorUserPKId, PageRequest pageRequest) {
        List<PublicTodoDto> publicTodoDtoList = new ArrayList<>();
        for(PublicTodoEntity entity : publicTodoRepository.findAllByAuthorUserId(authorUserPKId, pageRequest)){
            publicTodoDtoList.add(
                PublicTodoDto.fromEntity(
                    entity,
                    supportNumberCacheRepository.getSupportNumber(entity.getId()),
                    nagNumberCacheRepository.getNagNumber(entity.getId())
                )
            );
        }
        return publicTodoDtoList;
    }




    /**
     * 공개 투두 아이템을 추가한다. 레디스에도 응원 및 잔소리 기록용 키밸류 쌍을 저장한다.
     * */
    @Transactional
    public void addPublicTodo(Long authorUserPKId, PublicTodoCreateRequest publicTodoCreateRequest) {
        CommonUtils.validateContentLengthAndDeadlineDate(publicTodoCreateRequest.getPublicTodoContent(), publicTodoCreateRequest.getDeadlineDate());

        PublicTodoEntity publicTodoEntity = publicTodoRepository.save(
            PublicTodoEntity.builder()
                .authorUserId(authorUserPKId)
                .authorNickname(publicTodoCreateRequest.getAuthorUserNickname())
                .todoContent(publicTodoCreateRequest.getPublicTodoContent())
                .deadlineDate(publicTodoCreateRequest.getDeadlineDate())
                .finished(false)
                .build()
        );

        supportNumberCacheRepository.setInitialSupport(publicTodoEntity.getId());
        nagNumberCacheRepository.setInitialNag(publicTodoEntity.getId());
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
     * 삭제한 아이템에 대하여 응원/잔소리를 누른 정보를 모두 삭제해야 한다.
     * 레디스 리포지토리도 검사하여 해당 공개 투두 아이템의 응원 및 잔소리 수를 기록한 레디스 키-값 쌍 두 개를 제거해야 한다.
     * */
    @Transactional
    public void removePublicTodo(Long authorUserPKId, Long publicTodoPKId) {
        publicTodoRepository.deleteByIdAndAuthorUserId(publicTodoPKId, authorUserPKId);

        supportNumberCacheRepository.deleteSupportNumberInfo(publicTodoPKId);
        nagNumberCacheRepository.deleteNagNumberInfo(publicTodoPKId);

        supportRepository.deleteAllByPublishedTodoPKId(publicTodoPKId);

        nagRepository.deleteAllByPublishedTodoPKId(publicTodoPKId);
    }

}
















