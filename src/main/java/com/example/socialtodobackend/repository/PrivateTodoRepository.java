package com.example.socialtodobackend.repository;

import com.example.socialtodobackend.entity.PrivateTodoEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivateTodoRepository extends JpaRepository<PrivateTodoEntity, Long> {

    Slice<PrivateTodoEntity> findAllByAuthorUserId(Long authorUserId, PageRequest pageRequest);

}
