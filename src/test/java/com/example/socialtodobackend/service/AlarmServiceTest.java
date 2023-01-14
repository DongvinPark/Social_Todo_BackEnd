package com.example.socialtodobackend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.socialtodobackend.dto.alarm.AlarmDto;
import com.example.socialtodobackend.persist.AlarmEntity;
import com.example.socialtodobackend.persist.AlarmRepository;
import com.example.socialtodobackend.persist.UserEntity;
import com.example.socialtodobackend.persist.UserRepository;
import com.example.socialtodobackend.type.AlarmTypeCode;
import com.example.socialtodobackend.utils.CommonUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

@ExtendWith(MockitoExtension.class)
class AlarmServiceTest {

    @Mock
    private AlarmRepository alarmRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AlarmService alarmService;




    @Test
    @DisplayName("전체 알림 리스트 요청")
    void success_GetAlarmList(){
        //given
        AlarmEntity alarmEntity = AlarmEntity.builder()
            .alarmReceiverUserId(1L)
            .alarmType(AlarmTypeCode.SUPPORT)
            .build();

        List<AlarmEntity> alarmEntityList = new ArrayList<>();
        alarmEntityList.add(alarmEntity);

        Slice<AlarmEntity> slice = new PageImpl<>(alarmEntityList);

        given(alarmRepository.findAllByAlarmReceiverUserIdEquals(
            1L, PageRequest.of(0, CommonUtils.PAGE_SIZE)
        )).willReturn(slice);

        //when
        List<AlarmDto> alarmDtoList = alarmService.getAlarmList(1L, PageRequest.of(0, CommonUtils.PAGE_SIZE));

        //then
        assertEquals(1, alarmDtoList.size());
    }




    @Test
    @DisplayName("알림 1개 삭제 요청")
    void success_DeleteOneAlarm(){
        //given
        doNothing().when(alarmRepository).deleteByIdAndAlarmReceiverUserId(anyLong(), anyLong());

        //when
        alarmService.removeOneAlarm(1L, 1L);

        //then
        verify(alarmRepository, times(1)).deleteByIdAndAlarmReceiverUserId(1L, 1L);
    }




    @Test
    @DisplayName("알림 전체 삭제 요청")
    void success_DeleteAllAlarm(){
        //given
        doNothing().when(alarmRepository).deleteAllByAlarmReceiverUserIdEquals(anyLong());

        //when
        alarmService.removeAllAlarm(1L);

        //then
        verify(alarmRepository, times(1)).deleteAllByAlarmReceiverUserIdEquals(1L);
    }




    @Test
    @DisplayName("팔로우 발생시 알림 보내기")
    void success_sendFollowInfoAlarm(){
        //given
        UserEntity followSentUser = UserEntity.builder()
            .id(1L)
            .emailAddr("e1@mail.com")
            .password("1111")
            .nickname("one")
            .build();

        UserEntity followTargetUser = UserEntity.builder()
            .emailAddr("e2@mail.com")
            .password("1111")
            .nickname("two")
            .build();

        AlarmEntity followSenderAlarm = AlarmEntity.builder()
            .alarmType(AlarmTypeCode.FOLLOW)
            .alarmContent(CommonUtils.makeAlarmMessageWhenFollowedOtherUser("two"))
            .build();

        AlarmEntity followTargetAlarm = AlarmEntity.builder()
            .alarmSenderUserId(1L)
            .alarmType(AlarmTypeCode.FOLLOW)
            .alarmContent(CommonUtils.makeAlarmMessageWhenGetNewFollower("one"))
            .build();

        given(userRepository.findById(anyLong())).willReturn(Optional.of(followSentUser));

        given(userRepository.findById(anyLong())).willReturn(Optional.of(followTargetUser));

        given(alarmRepository.save(any())).willReturn(followSenderAlarm);

        given(alarmRepository.save(any())).willReturn(followTargetAlarm);

        //when
        alarmService.sendFollowInfoAlarm(1L, 2L);

        //then
        verify(userRepository, times(2)).findById(anyLong());
        verify(alarmRepository, times(2)).save(
            any()
        );
    }




    @Test
    @DisplayName("응원 이벤트 발생시 알림 보내기 - 기존 알림 없을 때")
    void success_sendSupportInfoAlarm_RelatedAlarmNotExists(){
        //given
        AlarmEntity alarmEntity = AlarmEntity.builder()
            .alarmReceiverUserId(1L)
            .alarmSenderUserId(2L)
            .numberOfPeopleRelatedToAlarm(1L)
            .relatedPublicTodoPKId(1L)
            .alarmType(AlarmTypeCode.SUPPORT)
            .alarmContent(CommonUtils.makeSupportAlarmMessage())
            .build();

        given(alarmRepository.findAlarmEntityByRelatedPublicTodoPKIdEqualsAndAlarmTypeEquals(anyLong(), any())).willReturn(Optional.empty());

        given(alarmRepository.save(any())).willReturn(alarmEntity);

        //when
        alarmService.sendNagInfoAlarm(2L, 1L, 1L);

        //then
        verify(alarmRepository, times(1)).findAlarmEntityByRelatedPublicTodoPKIdEqualsAndAlarmTypeEquals(anyLong(), any());
        verify(alarmRepository, times(1)).save(any());
    }




    @Test
    @DisplayName("응원 이벤트 발생시 알림 보내기 - 기존 알림 있을 때")
    void success_sendSupportInfoAlarm_RelatedAlarmExists(){
        //given
        AlarmEntity alarmEntity = AlarmEntity.builder()
            .alarmReceiverUserId(1L)
            .alarmSenderUserId(2L)
            .numberOfPeopleRelatedToAlarm(1L)
            .relatedPublicTodoPKId(1L)
            .alarmType(AlarmTypeCode.SUPPORT)
            .alarmContent(CommonUtils.makeSupportAlarmMessage())
            .build();

        given(alarmRepository.findAlarmEntityByRelatedPublicTodoPKIdEqualsAndAlarmTypeEquals(anyLong(), any())).willReturn(Optional.of(alarmEntity));

        given(alarmRepository.save(any())).willReturn(alarmEntity);

        //when
        alarmService.sendNagInfoAlarm(2L, 1L, 1L);

        //then
        verify(alarmRepository, times(1)).findAlarmEntityByRelatedPublicTodoPKIdEqualsAndAlarmTypeEquals(anyLong(), any());
        verify(alarmRepository, times(1)).save(any());
    }


}


















