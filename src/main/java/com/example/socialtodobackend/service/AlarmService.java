package com.example.socialtodobackend.service;

import com.example.socialtodobackend.dto.SupportNagDto;
import com.example.socialtodobackend.dto.alarm.AlarmDto;
import com.example.socialtodobackend.dto.follow.FollowDto;
import com.example.socialtodobackend.entity.AlarmEntity;
import com.example.socialtodobackend.entity.PublicTodoEntity;
import com.example.socialtodobackend.entity.UserEntity;
import com.example.socialtodobackend.exception.SocialTodoException;
import com.example.socialtodobackend.repository.AlarmRepository;
import com.example.socialtodobackend.repository.PublicTodoRepository;
import com.example.socialtodobackend.repository.UserRepository;
import com.example.socialtodobackend.type.AlarmTypeCode;
import com.example.socialtodobackend.type.ErrorCode;
import com.example.socialtodobackend.utils.CommonUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;
    private final PublicTodoRepository publicTodoRepository;



    /**
     * 특정 유저의 모든 알림을 가져온다.
     * 이것도 페이징이 필요하다.
     * */
    @Transactional(readOnly = true)
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





    /**
     * 특정 공개 투두 아이템에 대해서 응원을 눌렀을 때, 투두 아이템 작성자에게 알림을 보내는 기능.
     * 해당 공개 투투 아이템에 대한 첫 응원일 경우 알림을 새로 보내줘야 하고,
     * 이미 다른 사람이 응원을 누른적이 있다면, 해당알림을 찾아서 숫자를 +1 해준다.
     * */
    @Transactional
    public void sendSupportInfoAlarm(SupportNagDto supportNagDto) {
        //기존 알림이 있는지를 먼저 찾는다. 공개투두 아이템은 기존 알람이 없는 최초의 응원일 때만 찾아내면 된다.
        //기존 알림이 있는지 찾아낼 때는 알림 엔티티의 relatedPublicTodoPKId와 alarmType이 있으면 된다.
        //각각의 모든 공개 투두 아이템에 대하여 발생 가능한 응원 알림은 0개 또는 단 1 개만 가능하기 때문이다.
        Optional<AlarmEntity> optionalAlarmEntity = alarmRepository.findAlarmEntityByRelatedPublicTodoPKIdEqualsAndAlarmTypeEquals(
            supportNagDto.getPublicTodoPKId(), AlarmTypeCode.SUPPORT
        );
        if(optionalAlarmEntity.isPresent()){
            //기존 알림이 존재한다. 응원 해준 사람 숫자만 += 1 해주면 된다.
            AlarmEntity alarmEntity = optionalAlarmEntity.get();
            long supportSentUserNumber = alarmEntity.getNumberOfPeopleRelatedToAlarm();
            supportSentUserNumber++;
            alarmEntity.setNumberOfPeopleRelatedToAlarm(supportSentUserNumber);
            alarmRepository.save(alarmEntity);
        } else {
            //기존 알림이 존재하지 않으므로 새로 만들어야 한다.
            //이때는 알림을 받는 유저의 주키 아이디를 특정하기 위해서 publicTodoRepository를 검색해야 한다.
            PublicTodoEntity publicTodoEntity = publicTodoRepository.findById(supportNagDto.getPublicTodoPKId()).orElseThrow(
                () -> new SocialTodoException(ErrorCode.PUBLIC_TODO_NOT_FOUND)
            );

            alarmRepository.save(
                AlarmEntity.builder()
                    .alarmReceiverUserId(publicTodoEntity.getAuthorUserId())
                    .alarmSenderUserId(supportNagDto.getSupportNagSentUserPKId())
                    .numberOfPeopleRelatedToAlarm(1L)
                    .relatedPublicTodoPKId(supportNagDto.getPublicTodoPKId())
                    .alarmType(AlarmTypeCode.SUPPORT)
                    .alarmContent(CommonUtils.makeSupportAlarmMessage())
                    .build()
            );

        }
    }





    /**
     *
     * */
    @Transactional
    public void sendNagInfoAlarm(SupportNagDto supportNagDto) {
        //기존 알림이 있는지를 먼저 찾는다. 공개투두 아이템은 기존 알람이 없는 최초의 응원일 때만 찾아내면 된다.
        //기존 알림이 있는지 찾아낼 때는 알림 엔티티의 relatedPublicTodoPKId와 alarmType이 있으면 된다.
        //각각의 모든 공개 투두 아이템에 대하여 발생 가능한 잔소리 알림은 0개 또는 단 1 개만 가능하기 때문이다.
        Optional<AlarmEntity> optionalAlarmEntity = alarmRepository.findAlarmEntityByRelatedPublicTodoPKIdEqualsAndAlarmTypeEquals(
            supportNagDto.getPublicTodoPKId(), AlarmTypeCode.NAG
        );
        if(optionalAlarmEntity.isPresent()){
            //기존 알림이 존재한다. 잔소리 해준 사람 숫자만 += 1 해주면 된다.
            AlarmEntity alarmEntity = optionalAlarmEntity.get();
            long nagSentUserNumber = alarmEntity.getNumberOfPeopleRelatedToAlarm();
            nagSentUserNumber++;
            alarmEntity.setNumberOfPeopleRelatedToAlarm(nagSentUserNumber);
            alarmRepository.save(alarmEntity);
        } else {
            //기존 알림이 존재하지 않으므로 새로 만들어야 한다.
            //이때는 알림을 받는 유저의 주키 아이디를 특정하기 위해서 publicTodoRepository를 검색해야 한다.
            PublicTodoEntity publicTodoEntity = publicTodoRepository.findById(supportNagDto.getPublicTodoPKId()).orElseThrow(
                () -> new SocialTodoException(ErrorCode.PUBLIC_TODO_NOT_FOUND)
            );

            alarmRepository.save(
                AlarmEntity.builder()
                    .alarmReceiverUserId(publicTodoEntity.getAuthorUserId())
                    .alarmSenderUserId(supportNagDto.getSupportNagSentUserPKId())
                    .numberOfPeopleRelatedToAlarm(1L)
                    .relatedPublicTodoPKId(supportNagDto.getPublicTodoPKId())
                    .alarmType(AlarmTypeCode.NAG)
                    .alarmContent(CommonUtils.makeNagAlarmMessage())
                    .build()
            );
        }
    }

    //-------------- PRIVATE HELPER METHODS AREA ----------


}





















