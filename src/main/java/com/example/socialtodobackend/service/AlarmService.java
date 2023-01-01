package com.example.socialtodobackend.service;

import com.example.socialtodobackend.dto.AlarmDto;
import com.example.socialtodobackend.dto.FollowDto;
import com.example.socialtodobackend.entity.AlarmEntity;
import com.example.socialtodobackend.entity.UserEntity;
import com.example.socialtodobackend.exception.SocialTodoException;
import com.example.socialtodobackend.repository.AlarmRepository;
import com.example.socialtodobackend.repository.UserRepository;
import com.example.socialtodobackend.type.AlarmTypeCode;
import com.example.socialtodobackend.type.ErrorCode;
import com.example.socialtodobackend.utils.CommonUtils;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;


    /**
     * 특정 유저의 모든 알림을 가져온다.
     * */
    @Transactional
    public List<AlarmDto> getAlarmList(Long userPKId) {
        if(!userRepository.findById(userPKId).isPresent()){
            throw new SocialTodoException(ErrorCode.USER_NOT_FOUND);
        }

        List<AlarmDto> alarmDtoList = new ArrayList<>();

        for(AlarmEntity alarmEntity : alarmRepository.findAllByAlarmReceiverUserIdEquals(userPKId)){
            alarmDtoList.add(
                AlarmDto.fromEntity(alarmEntity)
            );
        }
        return alarmDtoList;
    }


    /**
     * 알림을 1개 삭제하고 그 후 남은 알람 리스트를 반환한다.
     * */
    @Transactional
    public boolean removeOneAlarm(Long alarmEntityPKId) {
        if(!alarmRepository.findById(alarmEntityPKId).isPresent()){
            throw new SocialTodoException(ErrorCode.ALARM_INFO_NOT_FOUND);
        }
        alarmRepository.deleteById(alarmEntityPKId);
        return true;
    }



    /**
     * 특정 유저의 모든 알림들을 삭제하고 빈 알람 리스트를 반환한다.
     * */
    @Transactional
    public List<AlarmDto> removeAllAlarm(Long userPKId) {
        if(!userRepository.findById(userPKId).isPresent()){
            throw new SocialTodoException(ErrorCode.USER_NOT_FOUND);
        }

        alarmRepository.deleteAllByAlarmReceiverUserIdEquals(userPKId);
        return new ArrayList<>();
    }


    /**
     * 한 유저가 다른 유저를 팔로우한 경우, 팔로우를 받은 유저와 팔로우를 한 유저 모두에게 알림을 주는 기능
     * */
    @Transactional
    public void sendFollowInfoAlarm(FollowDto followDto) {

        UserEntity followSentUserEntity = userRepository.findById(followDto.getFollowSentUserPKId()).orElseThrow(()-> new SocialTodoException(
            ErrorCode.USER_NOT_FOUND));

        UserEntity followReceiverUserEntity = userRepository.findById(followDto.getFollowReceivedUserPKId()).orElseThrow(()-> new SocialTodoException(ErrorCode.USER_NOT_FOUND));

        //일단 내가 팔로우를 보냈으므로, 나에게 ~~를 팔로우 했다는 내용을 구성하여 알림을 보낸다.
        alarmRepository.save(
            AlarmEntity.builder()
                .alarmReceiverUserId(followDto.getFollowSentUserPKId())
                .alarmType(AlarmTypeCode.FOLLOW)
                .alarmContent(CommonUtils.makeAlarmMessageWhenFollowedOtherUser(followReceiverUserEntity.getNickname()))
                .build()
        );

        //그 다음 상대방에게 내가 팔로우 했다는 내용을 구성하여 알림을 보낸다. 닉네임 쓰라.
        alarmRepository.save(
            AlarmEntity.builder()
                .alarmReceiverUserId(followDto.getFollowReceivedUserPKId())
                .alarmSenderUserId(followDto.getFollowSentUserPKId())
                .alarmType(AlarmTypeCode.FOLLOW)
                .alarmContent(CommonUtils.makeAlarmMessageWhenGetNewFollower(followSentUserEntity.getNickname()))
                .build()
        );
    }



    //-------------- PRIVATE HELPER METHODS AREA ----------

    /**
     * 하나의 공개 투두 아이템에 대하여 수 만 명 단위의 응원/잔소리가 있을 경우, 그 투두 아이템을 작성한 유저에게는
     * 수 만 개의 알림을 제공하는 것이 아니라, 한 개의 알림만을 사용하여 "~외 ? 명이 응원/잔소리를 했습니다"라고
     * 알려주는 로직을 사용하는 것이 사용성 측면에서 바람직하다.
     * */
    private boolean doesAlarmExists(){
        return false;
    }



}



















