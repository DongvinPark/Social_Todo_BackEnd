package com.example.socialtodobackend.service;

import com.example.socialtodobackend.dto.user.UserDto;
import com.example.socialtodobackend.persist.NagEntity;
import com.example.socialtodobackend.persist.NagRepository;
import com.example.socialtodobackend.persist.PublicTodoRepository;
import com.example.socialtodobackend.persist.UserRepository;
import com.example.socialtodobackend.utils.CommonUtils;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NagService {

    private final PublicTodoRepository publicTodoRepository;
    private final NagRepository nagRepository;
    private final UserRepository userRepository;


    /**
     * 잔소리를 하나 추가한다.
     * */
    @Transactional
    public void addNag(Long nagSentUserPKId, Long publicTodoPKId) {
        // 더 이상 공개 투두 아이템을 수정할 필요가 없다.

        // 대신 이때 발생한 일을 카프카 프로듀서에 누적시킨다.
        // 이때, 잔소리 리포지토리에 잔소리 누른 유저 엔티티를 저장해야 한다.

        nagRepository.save(
            NagEntity.builder()
                .publishedTodoPKId(publicTodoPKId)
                .nagSentUserPKId(nagSentUserPKId)
                .build()
        );
    }




    /**
     * 기존에 눌렀던 잔소리를 취소시킨다.
     * 취소 후 알림을 보내지는 않고, 취소시키기 이전의 잔소리로 인해서 전송된 알림에 대해서도 별도의 수정을 하지 않는다.
     * */
    @Transactional
    public void undoNag(Long nagSentUserPKId, Long publicTodoPKId) {
        //여기서도 더 이상 퍼블릭 투듀에 대한 이벤트를 처리할 필요가 없다.

        nagRepository.deleteByPublishedTodoPKIdAndNagSentUserPKId(
            publicTodoPKId, nagSentUserPKId
        );
    }



    /**
     * 특정한 공개 투두 아이템에 대하여 잔소리를 해준 사람들의 목록을 확인할 수 있다.
     * */
    @Transactional(readOnly = true)
    public List<UserDto> getAllNagSentUsers(Long publicTodoPKId, PageRequest pageRequest) {
        //우선, 잔소리를 해준 모든 유저들의 주키 아이디를 하나의 리스트로 모은다. 이때도 페이징 처리가 필요하다.
        List<Long> nagSentUserPKIdList = nagRepository.findAllByPublishedTodoPKId(publicTodoPKId, pageRequest).getContent().stream().map(NagEntity::getNagSentUserPKId).collect(Collectors.toList());

        //그후 유저 리포지토리에서 위에서 만든 주키 아이디 리스트에 포함되는 사람을 전부 찾아내서 페이징처리하여 보여준다.
        return userRepository.findAllByIdIn(nagSentUserPKIdList, PageRequest.of(0, CommonUtils.PAGE_SIZE)).getContent().stream().map(UserDto::fromEntity).collect(Collectors.toList());
    }



}





























