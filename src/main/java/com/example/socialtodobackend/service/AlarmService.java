package com.example.socialtodobackend.service;

import com.example.socialtodobackend.dto.alarm.AlarmDto;
import com.example.socialtodobackend.persist.AlarmEntity;
import com.example.socialtodobackend.persist.UserEntity;
import com.example.socialtodobackend.exception.SingletonException;
import com.example.socialtodobackend.persist.AlarmRepository;
import com.example.socialtodobackend.persist.PublicTodoRepository;
import com.example.socialtodobackend.persist.UserRepository;
import com.example.socialtodobackend.type.AlarmTypeCode;
import com.example.socialtodobackend.utils.CommonUtils;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
    public List<AlarmDto> getAlarmList(Long userPKId, PageRequest pageRequest) {
        if(!userRepository.findById(userPKId).isPresent()){
            throw SingletonException.USER_NOT_FOUND;
        }

        return alarmRepository.findAllByAlarmReceiverUserIdEquals(userPKId, pageRequest).getContent().stream().map(
            AlarmDto::fromEntity).collect(
            Collectors.toList());
    }



    /**
     * 알림을 1개 삭제한다.
     * */
    @Transactional
    public void removeOneAlarm(Long alarmEntityPKId, Long userPKId) {
        alarmRepository.deleteByIdAndAlarmReceiverUserId(alarmEntityPKId, userPKId);
    }



    /**
     * 특정 유저의 모든 알림들을 삭제한다.
     * */
    @Transactional
    public void removeAllAlarm(Long userPKId) {
        if(!userRepository.findById(userPKId).isPresent()){
            throw SingletonException.USER_NOT_FOUND;
        }

        alarmRepository.deleteAllByAlarmReceiverUserIdEquals(userPKId);
    }



    /**
     * 한 유저가 다른 유저를 팔로우한 경우, 팔로우를 받은 유저와 팔로우를 한 유저 모두에게 알림을 주는 기능
     * */
    @Transactional
    public void sendFollowInfoAlarm(Long followSentUserPKId, Long followRelationTargetUserPKId) {

        UserEntity followSentUserEntity = userRepository.findById(followSentUserPKId).orElseThrow(()-> SingletonException.USER_NOT_FOUND);

        UserEntity followReceiverUserEntity = userRepository.findById(followRelationTargetUserPKId).orElseThrow(()-> SingletonException.USER_NOT_FOUND);

        //일단 내가 팔로우를 보냈으므로, 나에게 ~~를 팔로우 했다는 내용을 구성하여 알림을 보낸다.
        alarmRepository.save(
            AlarmEntity.builder()
                .alarmReceiverUserId(followSentUserPKId)
                .alarmType(AlarmTypeCode.FOLLOW)
                .alarmContent(CommonUtils.makeAlarmMessageWhenFollowedOtherUser(followReceiverUserEntity.getNickname()))
                .build()
        );

        //그 다음 상대방에게 내가 팔로우 했다는 내용을 구성하여 알림을 보낸다. 닉네임 쓰라.
        alarmRepository.save(
            AlarmEntity.builder()
                .alarmReceiverUserId(followRelationTargetUserPKId)
                .alarmSenderUserId(followSentUserPKId)
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
    public void sendSupportInfoAlarm(Long supportSentUserPKId, Long publicTodoPKId, Long todoAuthorUserPKId) {
        //기존 알림이 있는지를 먼저 찾는다. 공개투두 아이템은 기존 알람이 없는 최초의 응원일 때만 찾아내면 된다.
        //기존 알림이 있는지 찾아낼 때는 알림 엔티티의 relatedPublicTodoPKId와 alarmType이 있으면 된다.
        //각각의 모든 공개 투두 아이템에 대하여 발생 가능한 응원 알림은 0개 또는 단 1 개만 가능하기 때문이다.
        Optional<AlarmEntity> optionalAlarmEntity = alarmRepository.findAlarmEntityByRelatedPublicTodoPKIdEqualsAndAlarmTypeEquals(
            publicTodoPKId, AlarmTypeCode.SUPPORT
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
            //프런트엔드에서 넘겨 받은 todoAuthorUserPKId를 이용해서 추가적인 DB I/O 없이 바로 새 알림을 저장한다.
            alarmRepository.save(
                AlarmEntity.builder()
                    .alarmReceiverUserId(todoAuthorUserPKId)
                    .alarmSenderUserId(supportSentUserPKId)
                    .numberOfPeopleRelatedToAlarm(1L)
                    .relatedPublicTodoPKId(publicTodoPKId)
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
    public void sendNagInfoAlarm(Long nagSentUserPKId, Long publicTodoPKId, Long todoAuthorUserPKId) {
        //기존 알림이 있는지를 먼저 찾는다. 공개투두 아이템은 기존 알람이 없는 최초의 응원일 때만 찾아내면 된다.
        //기존 알림이 있는지 찾아낼 때는 알림 엔티티의 relatedPublicTodoPKId와 alarmType이 있으면 된다.
        //각각의 모든 공개 투두 아이템에 대하여 발생 가능한 잔소리 알림은 0개 또는 단 1 개만 가능하기 때문이다.
        Optional<AlarmEntity> optionalAlarmEntity = alarmRepository.findAlarmEntityByRelatedPublicTodoPKIdEqualsAndAlarmTypeEquals(
            publicTodoPKId, AlarmTypeCode.NAG
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
            //프런트엔드에서 넘겨 받은 todoAuthorUserPKId를 이용해서 추가적인 DB I/O 없이 바로 새 알림을 저장한다.

            alarmRepository.save(
                AlarmEntity.builder()
                    .alarmReceiverUserId(todoAuthorUserPKId)
                    .alarmSenderUserId(nagSentUserPKId)
                    .numberOfPeopleRelatedToAlarm(1L)
                    .relatedPublicTodoPKId(publicTodoPKId)
                    .alarmType(AlarmTypeCode.NAG)
                    .alarmContent(CommonUtils.makeNagAlarmMessage())
                    .build()
            );
        }//else
    }


}





















