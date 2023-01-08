package com.example.socialtodobackend.service;

import com.example.socialtodobackend.dto.SupportNagDto;
import com.example.socialtodobackend.dto.UserDto;
import com.example.socialtodobackend.entity.NagEntity;
import com.example.socialtodobackend.entity.PublicTodoEntity;
import com.example.socialtodobackend.exception.SocialTodoException;
import com.example.socialtodobackend.repository.NagRepository;
import com.example.socialtodobackend.repository.PublicTodoRepository;
import com.example.socialtodobackend.repository.UserRepository;
import com.example.socialtodobackend.type.ErrorCode;
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
    public boolean addNag(SupportNagDto supportNagDto) {
        PublicTodoEntity publicTodoEntity = publicTodoRepository.findById(
            supportNagDto.getPublicTodoPKId()).orElseThrow(
            () -> new SocialTodoException(ErrorCode.PUBLIC_TODO_NOT_FOUND)
        );

        long nagNumber = publicTodoEntity.getNumberOfNag();
        nagNumber++;
        publicTodoEntity.setNumberOfNag(nagNumber);

        publicTodoRepository.save(publicTodoEntity);

        nagRepository.save(
            NagEntity.builder()
                .publishedTodoPKId(supportNagDto.getPublicTodoPKId())
                .nagSentUserPKId(supportNagDto.getSupportNagSentUserPKId())
                .build()
        );

        return true;
    }




    /**
     * 기존에 눌렀던 잔소리를 취소시킨다.
     * 취소 후 알림을 보내지는 않고, 취소시키기 이전의 잔소리로 인해서 전송된 알림에 대해서도 별도의 수정을 하지 않는다.
     * */
    @Transactional
    public boolean undoNag(SupportNagDto supportNagDto) {
        PublicTodoEntity publicTodoEntity = publicTodoRepository.findById(supportNagDto.getPublicTodoPKId()).orElseThrow(
            () -> new SocialTodoException(ErrorCode.PUBLIC_TODO_NOT_FOUND)
        );

        long nagNumber = publicTodoEntity.getNumberOfNag();
        if(nagNumber == 0) throw new SocialTodoException(ErrorCode.CANNOT_DECREASE_NAG_NUMBER_BELLOW_ZERO);
        nagNumber--;
        publicTodoEntity.setNumberOfNag(nagNumber);

        nagRepository.deleteByPublishedTodoPKIdAndNagSentUserPKId(
            supportNagDto.getPublicTodoPKId(), supportNagDto.getSupportNagSentUserPKId()
        );
        return true;
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





























