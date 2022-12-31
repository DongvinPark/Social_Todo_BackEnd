package com.example.socialtodobackend.service;

import com.example.socialtodobackend.dto.FollowDto;
import com.example.socialtodobackend.dto.UnfollowDto;
import com.example.socialtodobackend.dto.UserDto;
import com.example.socialtodobackend.entity.FollowEntity;
import com.example.socialtodobackend.entity.UserEntity;
import com.example.socialtodobackend.entity.UserFollowSendCountEntity;
import com.example.socialtodobackend.exception.SocialTodoException;
import com.example.socialtodobackend.repository.FollowRepository;
import com.example.socialtodobackend.repository.FollowSendCountRepository;
import com.example.socialtodobackend.repository.UserRepository;
import com.example.socialtodobackend.type.ErrorCode;
import com.example.socialtodobackend.utils.CommonUtils;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final FollowSendCountRepository followSendCountRepository;


    /**
     * 특정 유저를 팔로우한(==특정 유저에게 팔로우를 보낸) 모든 사람들을 확인한다.
     * */
    @Transactional
    public List<UserDto> getFollowers(Long userPKId) {
        List<UserDto> userDtoList = new ArrayList<>();
        //userPKID를 팔로우한(==userPKID가 팔로우를 받게 만든) 모든 사람들을 찾는다.
        for(FollowEntity followEntity : followRepository.findAllByFollowReceivedUserId(userPKId)){
            //userPKID를 팔로우한 사용자를 찾는다.
            UserEntity userEntity = userRepository.findById(followEntity.getFollowSentUserId()).orElseThrow(()->new SocialTodoException(ErrorCode.USER_NOT_FOUND));

            userDtoList.add(
                UserDto.builder()
                    .id(userEntity.getId())
                    .nickname(userEntity.getNickname())
                    .statusMessage(userEntity.getStatusMessage())
                    .registeredAt(CommonUtils.dateToString(userEntity.getRegisteredAt()))
                    .build()
            );
        }
        return userDtoList;
    }


    /**
     * 특정 유저가 팔로우를 한 모든 사람들을 확인한다.
     * */
    @Transactional
    public List<UserDto> getFollowees(Long userPKId) {
        List<UserDto> userDtoList = new ArrayList<>();
        //userPKID가 팔로우한 모든 사람들을 찾는다.
        for(FollowEntity followEntity : followRepository.findAllByFollowSentUserId(userPKId)){
            //userPKID로부터 팔로우를 받은 사람을 찾는다.
            UserEntity userEntity = userRepository.findById(followEntity.getFollowReceivedUserId()).orElseThrow(()->new SocialTodoException(ErrorCode.USER_NOT_FOUND));

            userDtoList.add(
                UserDto.builder()
                    .id(userEntity.getId())
                    .nickname(userEntity.getNickname())
                    .statusMessage(userEntity.getStatusMessage())
                    .registeredAt(CommonUtils.dateToString(userEntity.getRegisteredAt()))
                    .build()
            );
        }
        return userDtoList;
    }



    /**
     * 특정한 사용자가 다른 사용자를 팔로우한 이벤트를 처리한다.
     * 팔로우 할 수 있는 최대 사용자 숫자는 5000명이다.
     * */
    @Transactional
    public void addFollowInfo(FollowDto followDto) {
        //팔로우 카운트 엔티티를 가져온다. 처음으로 누를 팔로우일 경우, 팔로우 카운트 엔티티 정보를 새로 저장하고 카운트를 0으로 설정한다.
        UserFollowSendCountEntity userFollowSendCountEntity = followSendCountRepository.findById(followDto.getFollowSentUserPKId())
            .orElse(
                    followSendCountRepository.save(
                    UserFollowSendCountEntity.builder()
                        //주키를 팔로우 보낸 유저의 주키 아이디와 일치시켜서 셋팅해줘야 한다!!
                        .id_dependsOnFollowSentUserPK(followDto.getFollowSentUserPKId())
                        .userFollowSendCount(0L)
                        .build()
                    )
            );

        validateFollowLimit(userFollowSendCountEntity);

        long numberOfFollowedUsers = userFollowSendCountEntity.getUserFollowSendCount();
        numberOfFollowedUsers++;
        userFollowSendCountEntity.setUserFollowSendCount(numberOfFollowedUsers);
        followSendCountRepository.save(userFollowSendCountEntity);

        followRepository.save(
            FollowEntity.builder()
                .followSentUserId(followDto.getFollowSentUserPKId())
                .followReceivedUserId(followDto.getFollowReceivedUserPKId())
                .build()
        );
    }



    /**
     * 특정한 사용자가 다른 사용자를 언팔로우한 이벤트를 처리한다.
     * JPA는 삭제 동작에서 비효율적인 부분이 있으므로 이 부분은 나중에 JPA를 거치지 않고 DB에 바로
     * 쿼리를 전달하는 방식으로 효율화 해야 한다.
     * <br/>
     * 프런트 엔드에서 팔로우한 사람들 목록을 확인할 때, 백엔드에서 이미 followEntity의 주키 번호를 함께 전달하기
     * 때문에, 어떤 팔로우 정보를 삭제할지 탐색할 필요 없이, 해당 주키 아이디로 바로 삭제하면 된다.
     * 이렇게 할 수 있는 이유는 한 유저와 다른 유저 간의 팔로우 관계가 유일하기 때문이다.
     * 유저 간의 맞팔로우도 followRepository에서는 결국 서로 다른 튜플로 저장되기 때문에 문제 되지 않는다.
     * */
    @Transactional
    public void deleteFollowInfo(UnfollowDto unfollowDto) {
        //팔로우 정보 테이블에서 팔로우 정보를 삭제한다.
        followRepository.deleteById(unfollowDto.getId());

        //팔로우 카운트를 감소시킨다.
        UserFollowSendCountEntity userFollowSendCountEntity = followSendCountRepository.findById(
            unfollowDto.getFollowCanceledUserPKId()).orElseThrow(() -> new SocialTodoException(ErrorCode.USER_NOT_FOUND));

        validateFollowLimit(userFollowSendCountEntity);

        long followCount = userFollowSendCountEntity.getUserFollowSendCount();
        followCount--;
        userFollowSendCountEntity.setUserFollowSendCount(followCount);
        followSendCountRepository.save(userFollowSendCountEntity);
    }


    //------------- PRIVATE HELPER METHODS AREA ------------
    private void validateFollowLimit(UserFollowSendCountEntity userFollowSendCountEntity) {
        if(userFollowSendCountEntity.getUserFollowSendCount() == 0L){
            throw new SocialTodoException(ErrorCode.HAS_NO_USER_TO_UNFOLLOW);
        }

        if(userFollowSendCountEntity.getUserFollowSendCount() == CommonUtils.FOLLOW_LIMIT){
            throw new SocialTodoException(ErrorCode.CANNOT_FOLLOW_MORE_THAN_5000_USERS);
        }
    }

}

















