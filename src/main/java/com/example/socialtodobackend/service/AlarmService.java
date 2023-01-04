package com.example.socialtodobackend.service;

import com.example.socialtodobackend.dto.FollowDto;
import com.example.socialtodobackend.repository.UserRepository;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;



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


}
