package com.example.socialtodobackend.repository;

import com.example.socialtodobackend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {



}
