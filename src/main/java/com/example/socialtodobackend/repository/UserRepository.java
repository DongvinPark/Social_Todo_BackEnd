package com.example.socialtodobackend.repository;

import com.example.socialtodobackend.entity.UserEntity;
import java.util.Collection;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Slice<UserEntity> findAllByIdIn(Collection<Long> id, PageRequest pageRequest);

    Slice<UserEntity> findAllByNicknameContaining(String nickname, PageRequest pageRequest);

}
