package com.example.socialtodobackend.repository;

import com.example.socialtodobackend.entity.UserFollowSendCountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowSendCountRepository extends JpaRepository<UserFollowSendCountEntity, Long> {

}
