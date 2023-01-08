package com.example.socialtodobackend.repository;

import com.example.socialtodobackend.entity.UserEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    List<UserEntity> findAllByNicknameContaining(String nickname);

}
