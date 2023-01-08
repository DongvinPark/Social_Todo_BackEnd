package com.example.socialtodobackend.service;

import com.example.socialtodobackend.dto.follow.FollowDto;
import com.example.socialtodobackend.dto.follow.UserFollowInfoDto;
import com.example.socialtodobackend.entity.FollowEntity;
import com.example.socialtodobackend.entity.UserEntity;
import com.example.socialtodobackend.entity.UserFollowSendCountEntity;
import com.example.socialtodobackend.exception.SocialTodoException;
import com.example.socialtodobackend.repository.FollowRepository;
import com.example.socialtodobackend.repository.UserRepository;
import com.example.socialtodobackend.type.ErrorCode;
import com.example.socialtodobackend.utils.CommonUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    //private final FollowSendCountRepository followSendCountRepository;


    /**
     * 특정 유저를 팔로우한(==특정 유저에게 팔로우를 보낸) 모든 사람들을 확인한다.
     * 이 숫자는 수 천 만 명이 될 수도 있기 때문에 DB I/O를 최소화하고 페이징 로직을 도입해야 한다.
     * */
    @Transactional(readOnly = true)
    public List<UserFollowInfoDto> getFollowers(Long userPKId) {
        List<UserFollowInfoDto> userDtoList = new ArrayList<>();
        //userPKID를 팔로우한(==userPKID가 팔로우를 받게 만든) 모든 사람들을 찾는다.
        for(FollowEntity followEntity : followRepository.findAllByFollowReceivedUserId(userPKId)){
            //userPKID를 팔로우한 사용자를 찾는다.
            UserEntity userEntity = userRepository.findById(followEntity.getFollowSentUserId()).orElseThrow(()->new SocialTodoException(ErrorCode.USER_NOT_FOUND));

            userDtoList.add(
                UserFollowInfoDto.fromEntity(userEntity, followEntity)
            );
        }
        return userDtoList;
    }


    /**
     * 특정 유저가 팔로우를 한 모든 사람들을 확인한다.
     * 이 숫자는 최대 5000명 까지만 가능하므로 비교적 작은 숫자에 속하지만 페이징 로직은 필수적이다.
     * */
    @Transactional(readOnly = true)
    public List<UserFollowInfoDto> getFollowees(Long userPKId) {
        List<UserFollowInfoDto> userDtoList = new ArrayList<>();
        //userPKID가 팔로우한 모든 사람들을 찾는다.
        for(FollowEntity followEntity : followRepository.findAllByFollowSentUserId(userPKId)){
            //userPKID로부터 팔로우를 받은 사람을 찾는다.
            UserEntity userEntity = userRepository.findById(followEntity.getFollowReceivedUserId()).orElseThrow(()->new SocialTodoException(ErrorCode.USER_NOT_FOUND));

            userDtoList.add(
                UserFollowInfoDto.fromEntity(userEntity, followEntity)
            );
        }
        return userDtoList;
    }



    /**
     * 특정한 사용자가 다른 사용자를 팔로우한 이벤트를 처리한다.
     * 팔로우 할 수 있는 최대 사용자 숫자는 5000명이다.
     * */
    @Transactional
    public boolean addFollowInfo(FollowDto followDto) {
        validateFollowRelatedUsers(followDto);

        if(followRepository.countAllByFollowSentUserId(followDto.getFollowSentUserPKId()).equals(CommonUtils.FOLLOW_LIMIT)){
            throw new SocialTodoException(ErrorCode.CANNOT_FOLLOW_MORE_THAN_5000_USERS);
        }

        followRepository.save(
            FollowEntity.builder()
                .followSentUserId(followDto.getFollowSentUserPKId())
                .followReceivedUserId(followDto.getFollowReceivedUserPKId())
                .build()
        );
        return true;
    }



    /**
     * 특정한 사용자가 다른 사용자를 언팔로우한 이벤트를 처리한다.
     * <br/>
     * 프런트 엔드에서 팔로우한 사람들 목록을 확인할 때, 백엔드에서 UserFollowInfoDto 내의 pkIdInFollowEntity 필드에 followEntity의 주키 번호를 함께 전달하기 때문에, 어떤 팔로우 정보를 삭제할지 탐색할 필요 없이, 해당 주키 아이디로 바로 삭제하면 된다.
     * 이렇게 할 수 있는 이유는 한 유저와 다른 유저 간의 팔로우 관계가 유일하기 때문이다.
     * 유저 두 명 간의 맞팔로우도 followRepository에서는 결국 서로 다른 튜플로 저장되기 때문에 문제 되지 않는다.
     * */
    @Transactional
    public void deleteFollowInfo(Long followEntityPKId) {
        //팔로우 정보 테이블에서 팔로우 정보가 있을 경우 삭제한다.
        if(!followRepository.existsById(followEntityPKId)){
            throw new SocialTodoException(ErrorCode.FOLLOW_INFO_NOT_FOUND);
        }
        followRepository.deleteById(followEntityPKId);

    }



    //------------- PRIVATE HELPER METHODS AREA ------------



    /**
     * followSendCountRepository 를 쓸 생각이 없다면 이 메서드도 사실상 필요가 없다.
     * 추후 테스트 후 삭제 여부 결정한다.
     * */
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
            !userRepository.existsById(followDto.getFollowSentUserPKId())
        ) throw new SocialTodoException(ErrorCode.USER_NOT_FOUND);
        if(
            !userRepository.existsById(followDto.getFollowReceivedUserPKId())
        ) throw new SocialTodoException(ErrorCode.USER_NOT_FOUND);
    }

}

















