package com.example.socialtodobackend.service;

import com.example.socialtodobackend.dto.SupportNagDto;
import com.example.socialtodobackend.dto.UserDto;
import com.example.socialtodobackend.entity.PublicTodoEntity;
import com.example.socialtodobackend.entity.SupportEntity;
import com.example.socialtodobackend.entity.UserEntity;
import com.example.socialtodobackend.exception.SocialTodoException;
import com.example.socialtodobackend.repository.PublicTodoRepository;
import com.example.socialtodobackend.repository.SupportRepository;
import com.example.socialtodobackend.repository.UserRepository;
import com.example.socialtodobackend.type.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
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
    public boolean addSupport(SupportNagDto supportNagDto) {
        PublicTodoEntity publicTodoEntity = publicTodoRepository.findById(supportNagDto.getPublicTodoPKId()).orElseThrow(
            () -> new SocialTodoException(ErrorCode.PUBLIC_TODO_NOT_FOUND)
        );

        long supportNumber = publicTodoEntity.getNumberOfSupport();
        supportNumber++;
        publicTodoEntity.setNumberOfSupport(supportNumber);

        publicTodoRepository.save(publicTodoEntity);

        supportRepository.save(
            SupportEntity.builder()
                .publishedTodoPKId(supportNagDto.getPublicTodoPKId())
                .supportSentUserPKId(supportNagDto.getSupportNagSentUserPKId())
                .build()
        );

        return true;
    }




    /**
     * 기존에 눌렀던 응원을 취소시킨다.
     * 취소 후 따로 알림을 보내지는 않고, 취소시키기 이전의 응원으로 인해서 전송된 알림에 대해서도 별도의 수정을 하지 않는다.
     * */
    @Transactional
    public boolean undoSupport(SupportNagDto supportNagDto) {
        PublicTodoEntity publicTodoEntity = publicTodoRepository.findById(supportNagDto.getPublicTodoPKId()).orElseThrow(
            () -> new SocialTodoException(ErrorCode.PUBLIC_TODO_NOT_FOUND)
        );

        long supportNumber = publicTodoEntity.getNumberOfSupport();
        if(supportNumber == 0) throw new SocialTodoException(ErrorCode.CANNOT_DECREASE_SUPPORT_NUMBER_BELLOW_ZERO);
        supportNumber--;
        publicTodoEntity.setNumberOfSupport(supportNumber);

        publicTodoRepository.save(publicTodoEntity);

        supportRepository.deleteByPublishedTodoPKIdAndSupportSentUserPKId(
            supportNagDto.getPublicTodoPKId(), supportNagDto.getSupportNagSentUserPKId()
        );
        return true;
    }



    /**
     * 특정한 공개 투두 아이템에 대하여 응원을 해준 사람들의 목록을 확인할 수 있다.
     * */
    @Transactional(readOnly = true)
    public List<UserDto> getAllSupportSentUsers(Long publicTodoPKId) {
        List<UserDto> supportUserDtoList = new ArrayList<>();

        //특정 공개 투두 아이템에 대하여 응원을 눌러준 모든 유저들의 주키 아이디를 모은다.
        List<Long> userPKList = supportRepository.findAllByPublishedTodoPKId(publicTodoPKId).stream().mapToLong(SupportEntity::getSupportSentUserPKId).boxed().collect(Collectors.toList());

        for(UserEntity userEntity : userRepository.findAllById(userPKList)){
            supportUserDtoList.add(
                UserDto.fromEntity(userEntity)
            );
        }
        return supportUserDtoList;
    }


}





















