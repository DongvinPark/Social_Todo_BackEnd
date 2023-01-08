package com.example.socialtodobackend.repository;

import com.example.socialtodobackend.entity.PrivateTodoEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivateTodoRepository extends JpaRepository<PrivateTodoEntity, Long> {

    List<PrivateTodoEntity> findAllByAuthorUserId(Long authorUserId);

}
