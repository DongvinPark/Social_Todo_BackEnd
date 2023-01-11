package com.example.socialtodobackend.repository;

import com.example.socialtodobackend.entity.FollowEntity;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<FollowEntity, Long> {

    /**
     * 파라미터로 받은 유저가 팔로우한 다른 유저들을 찾을 때 사용하되, 페이징이 포함돼 있다.
     * 즉, userPKId가 팔로잉하고 있는 사람들은 누구인가?
     * */
    Slice<FollowEntity> findAllByFollowSentUserId(Long userPKId, PageRequest pageRequest);

    /**
     * 파라미터로 받은 유저가 팔로우한 다른 유저들을 찾을 때 사용하되, 페이징 처리 없이 전부다 찾아낸다.
     * 대신, 시스템 내부 로직으로 인해서 최댓값 5,000을 초과할 수 없다.
     * 이 최댓값은 CommonUtils.java 파일에 서술돼 있다.
     * */
    List<FollowEntity> findAllByFollowSentUserId(Long userPKId);

    /**
     * 파라미터로 받은 유저를 팔로우 하고 있는 다른 유저들을 찾을 때 사용한다.
     * 즉, userPKId의 팔로워들은 누구인가?
     * */
    Slice<FollowEntity> findAllByFollowReceivedUserId(Long userPKId, PageRequest pageRequest);

    /**
     * 특정 유저가 팔로우 하고 있는 다른 유저의 숫자를 리턴한다.
     * */
    Long countAllByFollowSentUserId(Long userPKId);

    /**
     * 팔로우 관계 정보를 삭제할 때 사용한다.
     * */
    void deleteByFollowSentUserIdEqualsAndFollowReceivedUserIdEquals(Long followSentUserId,
        Long followReceivedUserId);
}