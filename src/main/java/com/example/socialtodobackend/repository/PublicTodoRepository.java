package com.example.socialtodobackend.repository;

import com.example.socialtodobackend.entity.PublicTodoEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublicTodoRepository extends JpaRepository<PublicTodoEntity, Long> {

    List<PublicTodoEntity> findAllByAuthorUserId(Long authorUserPKId);

}
