package com.example.socialtodobackend.repository;

import com.example.socialtodobackend.entity.PublicTodoEntity;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicTodoRepository extends JpaRepository<PublicTodoEntity, Long> {

    List<PublicTodoEntity> findAllByAuthorUserId(Long authorUserPKId);

    List<PublicTodoEntity> findAllByFinishedIsFalseAndDeadlineDateEqualsAndAuthorUserIdIn(
        LocalDate deadlineDate, Collection<Long> authorUserId);

}
