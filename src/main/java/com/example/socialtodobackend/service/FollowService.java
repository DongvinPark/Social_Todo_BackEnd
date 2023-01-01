package com.example.socialtodobackend.service;

import com.example.socialtodobackend.dto.FollowDto;
import com.example.socialtodobackend.dto.UserFollowInfoDto;
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
    public List<UserFollowInfoDto> getFollowers(Long userPKId) {
        List<UserFollowInfoDto> userDtoList = new ArrayList<>();
        //userPKID를 팔로우한(==userPKID가 팔로우를 받게 만든) 모든 사람들을 찾는다.
        for(FollowEntity followEntity : followRepository.findAllByFollowReceivedUserId(userPKId)){
            //userPKID를 팔로우한 사용자를 찾는다.
            UserEntity userEntity = userRepository.findById(followEntity.getFollowSentUserId()).orElseThrow(()->new SocialTodoException(ErrorCode.USER_NOT_FOUND));

            userDtoList.add(
                UserFollowInfoDto.builder()
                    .id(userEntity.getId())
                    .pkIdInFollowEntity(followEntity.getId())
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
    public List<UserFollowInfoDto> getFollowees(Long userPKId) {
        List<UserFollowInfoDto> userDtoList = new ArrayList<>();
        //userPKID가 팔로우한 모든 사람들을 찾는다.
        for(FollowEntity followEntity : followRepository.findAllByFollowSentUserId(userPKId)){
            //userPKID로부터 팔로우를 받은 사람을 찾는다.
            UserEntity userEntity = userRepository.findById(followEntity.getFollowReceivedUserId()).orElseThrow(()->new SocialTodoException(ErrorCode.USER_NOT_FOUND));

            userDtoList.add(
                UserFollowInfoDto.builder()
                    .id(userEntity.getId())
                    .pkIdInFollowEntity(followEntity.getId())
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
        validateFollowRelatedUsers(followDto);

        //팔로우 카운트 엔티티를 가져온다. 회원가입이 완료되는 즉시 해당 유저에게 대응되는 UserFollowSendCountEntity 또한 followSendCountRepository에 저장되기 때문에 여기에서 탐색이 실패할 가능성은 거의 없다.
        UserFollowSendCountEntity userFollowSendCountEntity = followSendCountRepository.findById(followDto.getFollowSentUserPKId())
            .orElseThrow(
                ()->new SocialTodoException(ErrorCode.USER_NOT_FOUND)
            );

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
     * 프런트 엔드에서 팔로우한 사람들 목록을 확인할 때, 백엔드에서 UserFollowInfoDto 내의 pkIdInFollowEntity 필드에 followEntity의 주키 번호를 함께 전달하기 때문에, 어떤 팔로우 정보를 삭제할지 탐색할 필요 없이, 해당 주키 아이디로 바로 삭제하면 된다.
     * 이렇게 할 수 있는 이유는 한 유저와 다른 유저 간의 팔로우 관계가 유일하기 때문이다.
     * 유저 두 명 간의 맞팔로우도 followRepository에서는 결국 서로 다른 튜플로 저장되기 때문에 문제 되지 않는다.
     * */
    @Transactional
    public void deleteFollowInfo(Long followEntityPKId) {
        //팔로우 정보 테이블에서 팔로우 정보를 찾아낸 후 삭제한다.
        FollowEntity deleteTargetEntity = followRepository.findById(followEntityPKId).orElseThrow(
            ()-> new SocialTodoException(ErrorCode.FOLLOW_INFO_NOT_FOUND)
        );
        followRepository.deleteById(followEntityPKId);

        //팔로우 카운트를 감소시킨다. 언팔로우 신청한 사용자의 주키 아이디가 필요하다.
        UserFollowSendCountEntity userFollowSendCountEntity = followSendCountRepository.findById(deleteTargetEntity.getFollowSentUserId()).orElseThrow(() -> new SocialTodoException(ErrorCode.USER_NOT_FOUND));

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



    private void validateFollowRelatedUsers(FollowDto followDto){
        if(
            !userRepository.findById(followDto.getFollowSentUserPKId()).isPresent()
        ) throw new SocialTodoException(ErrorCode.USER_NOT_FOUND);
        if(
            !userRepository.findById(followDto.getFollowReceivedUserPKId()).isPresent()
        ) throw new SocialTodoException(ErrorCode.USER_NOT_FOUND);
    }

}

















