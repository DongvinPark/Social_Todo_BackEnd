package com.example.socialtodobackend.service;

import com.example.socialtodobackend.dto.follow.UserFollowInfoDto;
import com.example.socialtodobackend.exception.SingletonException;
import com.example.socialtodobackend.persist.FollowEntity;
import com.example.socialtodobackend.persist.FollowRepository;
import com.example.socialtodobackend.persist.UserRepository;
import com.example.socialtodobackend.persist.redis.FolloweeListCacheRepository;
import com.example.socialtodobackend.utils.CommonUtils;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final FolloweeListCacheRepository followeeListCacheRepository;


    /**
     * 특정 유저를 팔로우한(==특정 유저에게 팔로우를 보낸) 모든 사람들을 확인한다.
     * 이 숫자는 수 천 만 명이 될 수도 있기 때문에 DB I/O를 최소화하고 페이징 로직을 도입해야 한다.
     * */
    @Transactional(readOnly = true)
    public List<UserFollowInfoDto> getFollowers(Long userPKId, PageRequest pageRequest) {
        //userPKID를 팔로우한(==userPKID가 팔로우를 받게 만든) 모든 사람들의 주키 아이디 값을 찾는다.
        List<Long> followSentUserPKIdList = followRepository.findAllByFollowReceivedUserId(userPKId, pageRequest).getContent().stream().map(FollowEntity::getFollowSentUserId).collect(Collectors.toList());

        //유저 리포지토리에서 위에서 만든 주키 아이디 리스트에 포함되는 사람들을 페이징 처리하여 보여준다.
        return userRepository.findAllByIdIn(followSentUserPKIdList, PageRequest.of(0, CommonUtils.PAGE_SIZE)).getContent().stream().map(UserFollowInfoDto::fromEntity).collect(Collectors.toList());
    }


    /**
     * 특정 유저가 팔로우를 한 모든 사람들을 확인한다. 캐시를 먼저 보고, 없다면 DB를 본 후에 캐시 등록도 마친다.
     * 여기서도 페이징 로직을 적용한다.
     * */
    @Transactional(readOnly = true)
    public List<UserFollowInfoDto> getFollowees(Long userPKId, PageRequest pageRequest) {
        //userPKID가 팔로우한 모든 사람들의 주키 아이디 리스트를 만들어둔다.
        //이 값은 5,000을 초과할 수는 없지만, 바로 위의 getFollowers() 메서드에서 설명한 것과 동일한 이유로 여기서도 페이징을 적용한다.
        List<Long> followReceivedUserPKIdList = followRepository.findAllByFollowSentUserId(userPKId, pageRequest).getContent().stream().map(FollowEntity::getFollowReceivedUserId).collect(Collectors.toList());


        //유저 리포지토리에서 위에서 만든 주키 아이디 리스트에 포함되는 사람들을 페이징 처리하여 보여준다.
        return userRepository.findAllByIdIn(followReceivedUserPKIdList, PageRequest.of(0, CommonUtils.PAGE_SIZE)).getContent().stream().map(UserFollowInfoDto::fromEntity).collect(Collectors.toList());
    }



    /**
     * 특정한 사용자가 다른 사용자를 팔로우한 이벤트를 처리한다.
     * 팔로우 할 수 있는 최대 사용자 숫자는 5000명이다.
     * */
    @Transactional
    public void addFollowInfo(Long followSentUserPKId, Long followRelationTargetUserPKId) {
        validateFollowRelatedUser(followRelationTargetUserPKId);

        if(followRepository.countAllByFollowSentUserId(followSentUserPKId).equals(CommonUtils.FOLLOW_LIMIT)){
            throw SingletonException.CANNOT_FOLLOW_MORE_THAN_5000_USERS;
        }

        //캐시 히트라면, 캐시에 저장해준다.
        if(followeeListCacheRepository.isFolloweeListCacheHit(followSentUserPKId)){
            followeeListCacheRepository.addNewFollowee(followSentUserPKId, followRelationTargetUserPKId);
        }

        followRepository.save(
            FollowEntity.builder()
                .followSentUserId(followSentUserPKId)
                .followReceivedUserId(followRelationTargetUserPKId)
                .build()
        );
    }



    /**
     * 특정한 사용자가 다른 사용자를 언팔로우한 이벤트를 처리한다.
     * 팔로우 정보 테이블에서 팔로우 정보가 있을 경우 삭제한다.
     * */
    @Transactional
    public void deleteFollowInfo(Long requestUserPKId, Long unfollowTargetUserPKId) {
        followRepository.deleteByFollowSentUserIdEqualsAndFollowReceivedUserIdEquals(requestUserPKId, unfollowTargetUserPKId);

    }



    private void validateFollowRelatedUser(Long followRelationTargetUserPKId){
        //인증 생략 불가능. followRelationTargetUserPKId 는 JwtAuthenticationFilter에서 필터링을 거치지 않고 바로 RequestParam으로 들어오는 변수이기 때문이다.
        if(!userRepository.existsById(followRelationTargetUserPKId))throw SingletonException.USER_NOT_FOUND;

    }

}

















