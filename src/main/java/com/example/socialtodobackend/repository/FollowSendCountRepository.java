package com.example.socialtodobackend.repository;

import com.example.socialtodobackend.entity.UserFollowSendCountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowSendCountRepository extends JpaRepository<UserFollowSendCountEntity, Long> {

}
