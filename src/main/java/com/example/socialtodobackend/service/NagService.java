package com.example.socialtodobackend.service;

import com.example.socialtodobackend.dto.SupportNagDto;
import com.example.socialtodobackend.dto.UserDto;
import com.example.socialtodobackend.entity.NagEntity;
import com.example.socialtodobackend.entity.SupportNagNumberEntity;
import com.example.socialtodobackend.entity.UserEntity;
import com.example.socialtodobackend.exception.SocialTodoException;
import com.example.socialtodobackend.repository.NagRepository;
import com.example.socialtodobackend.repository.PublicTodoSupportNagNumberRepository;
import com.example.socialtodobackend.repository.UserRepository;
import com.example.socialtodobackend.type.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NagService {

    private final PublicTodoSupportNagNumberRepository publicTodoSupportNagNumberRepository;
    private final NagRepository nagRepository;
    private final UserRepository userRepository;


    /**
     * 잔소리를 하나 추가한다.
     * */
    @Transactional
    public boolean addNag(SupportNagDto supportNagDto) {
        SupportNagNumberEntity supportNagNumberEntity = publicTodoSupportNagNumberRepository.findById(
            supportNagDto.getPublicTodoPKId()).orElseThrow(
            () -> new SocialTodoException(ErrorCode.PUBLIC_TODO_NOT_FOUND)
        );

        long nagNumber = supportNagNumberEntity.getNumberOfNag();
        nagNumber++;
        supportNagNumberEntity.setNumberOfNag(nagNumber);
        publicTodoSupportNagNumberRepository.save(supportNagNumberEntity);

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
        SupportNagNumberEntity supportNagNumberEntity = publicTodoSupportNagNumberRepository.findById(supportNagDto.getPublicTodoPKId()).orElseThrow(
            () -> new SocialTodoException(ErrorCode.PUBLIC_TODO_NOT_FOUND)
        );

        long nagNumber = supportNagNumberEntity.getNumberOfNag();
        if(nagNumber == 0) throw new SocialTodoException(ErrorCode.CANNOT_DECREASE_NAG_NUMBER_BELLOW_ZERO);
        nagNumber--;
        supportNagNumberEntity.setNumberOfNag(nagNumber);
        publicTodoSupportNagNumberRepository.save(supportNagNumberEntity);

        nagRepository.deleteByPublishedTodoPKIdAndNagSentUserPKId(
            supportNagDto.getPublicTodoPKId(), supportNagDto.getSupportNagSentUserPKId()
        );
        return true;
    }



    /**
     * 특정한 공개 투두 아이템에 대하여 응원을 해준 사람들의 목록을 확인할 수 있다.
     * */
    public List<UserDto> getAllNagSentUsers(Long publicTodoPKId) {
        List<UserDto> nagUserDtoList = new ArrayList<>();

        //특정 공개 투두 아이템에 대하여 잔소리 눌러준 모든 유저들의 주키 아이디를 모은다.
        List<Long> userPKList = nagRepository.findAllByPublishedTodoPKId(publicTodoPKId).stream().mapToLong(
            NagEntity::getNagSentUserPKId).boxed().collect(
            Collectors.toList());

        for(UserEntity userEntity : userRepository.findAllById(userPKList)){
            nagUserDtoList.add(
                UserDto.fromEntity(userEntity)
            );
        }
        return nagUserDtoList;
    }


}





























