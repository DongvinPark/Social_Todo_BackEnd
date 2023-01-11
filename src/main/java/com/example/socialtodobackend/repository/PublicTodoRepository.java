package com.example.socialtodobackend.repository;

import com.example.socialtodobackend.entity.PublicTodoEntity;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicTodoRepository extends JpaRepository<PublicTodoEntity, Long> {

    Optional<PublicTodoEntity> findByIdAndAuthorUserId(Long id, Long authorUserId);

    void deleteByIdAndAuthorUserId(Long id, Long authorUserId);

    Slice<PublicTodoEntity> findAllByAuthorUserId(Long authorUserPKId, PageRequest pageRequest);

    Slice<PublicTodoEntity> findAllByFinishedIsFalseAndDeadlineDateEqualsAndAuthorUserIdIn(
        LocalDate deadlineDate, Collection<Long> authorUserId, PageRequest pageRequest
    );

}