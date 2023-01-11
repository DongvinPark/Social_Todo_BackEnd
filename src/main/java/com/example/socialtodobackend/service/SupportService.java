package com.example.socialtodobackend.service;

import com.example.socialtodobackend.dto.user.UserDto;
import com.example.socialtodobackend.entity.PublicTodoEntity;
import com.example.socialtodobackend.entity.SupportEntity;
import com.example.socialtodobackend.exception.SingletonException;
import com.example.socialtodobackend.repository.PublicTodoRepository;
import com.example.socialtodobackend.repository.SupportRepository;
import com.example.socialtodobackend.repository.UserRepository;
import com.example.socialtodobackend.utils.CommonUtils;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SupportService {

    private final PublicTodoRepository publicTodoRepository;
    private final SupportRepository supportRepository;
    private final UserRepository userRepository;



    /**
     * 응원을 하나 누른다.
     * */
    @Transactional
    public void addSupport(Long supportSentUserPKId, Long publicTodoPKId) {
        PublicTodoEntity publicTodoEntity = publicTodoRepository.findById(publicTodoPKId).orElseThrow(
            () -> SingletonException.PUBLIC_TODO_NOT_FOUND
        );

        long supportNumber = publicTodoEntity.getNumberOfSupport();
        supportNumber++;
        publicTodoEntity.setNumberOfSupport(supportNumber);

        publicTodoRepository.save(publicTodoEntity);

        supportRepository.save(
            SupportEntity.builder()
                .publishedTodoPKId(publicTodoPKId)
                .supportSentUserPKId(supportSentUserPKId)
                .build()
        );
    }




    /**
     * 기존에 눌렀던 응원을 취소시킨다.
     * 취소 후 따로 알림을 보내지는 않고, 취소시키기 이전의 응원으로 인해서 전송된 알림에 대해서도 별도의 수정을 하지 않는다.
     * */
    @Transactional
    public void undoSupport(Long supportSentUserPKId, Long publicTodoPKId) {
        PublicTodoEntity publicTodoEntity = publicTodoRepository.findById(publicTodoPKId).orElseThrow(
            () -> SingletonException.PUBLIC_TODO_NOT_FOUND
        );

        long supportNumber = publicTodoEntity.getNumberOfSupport();
        if(supportNumber == 0) throw SingletonException.CANNOT_DECREASE_SUPPORT_NUMBER_BELLOW_ZERO;
        supportNumber--;
        publicTodoEntity.setNumberOfSupport(supportNumber);

        publicTodoRepository.save(publicTodoEntity);

        supportRepository.deleteByPublishedTodoPKIdAndSupportSentUserPKId(
            publicTodoPKId, supportSentUserPKId
        );
    }



    /**
     * 특정한 공개 투두 아이템에 대하여 응원을 해준 사람들의 목록을 확인할 수 있다.
     * */
    @Transactional(readOnly = true)
    public List<UserDto> getAllSupportSentUsers(Long publicTodoPKId, PageRequest pageRequest) {
        //우선, 응원을 해준 모든 유저들의 주키 아이디를 하나의 리스트로 모은다. 이때도 페이징 처리가 필요하다.
        List<Long> supportSentUserPKIdList = supportRepository.findAllByPublishedTodoPKId(publicTodoPKId, pageRequest).getContent().stream().map(SupportEntity::getSupportSentUserPKId).collect(Collectors.toList());

        //그후 유저 리포지토리에서 위에서 만든 주키 아이디 리스트에 포함되는 사람을 전부 찾아내서 페이징처리하여 보여준다.
        return userRepository.findAllByIdIn(supportSentUserPKIdList, PageRequest.of(0, CommonUtils.PAGE_SIZE)).getContent().stream().map(UserDto::fromEntity).collect(Collectors.toList());
    }


}





















