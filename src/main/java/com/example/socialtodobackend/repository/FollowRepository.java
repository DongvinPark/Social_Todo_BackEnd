package com.example.socialtodobackend.repository;

import com.example.socialtodobackend.entity.FollowEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<FollowEntity, Long> {

    /**
     * 파라미터로 받은 유저가 팔로우한 다른 유저들을 찾을 때 사용한다.
     * 즉, userPKId가 팔로잉하고 있는 사람들은 누구인가?
     * */
    List<FollowEntity> findAllByFollowSentUserId(Long userPKId);

    /**
     * 파라미터로 받은 유저를 팔로우 하고 있는 다른 유저들을 찾을 때 사용한다.
     * 즉, userPKId의 팔로워들은 누구인가?
     * */
    List<FollowEntity> findAllByFollowReceivedUserId(Long userPKId);

}
