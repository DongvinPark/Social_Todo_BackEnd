package com.example.socialtodobackend.persist;

import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivateTodoRepository extends JpaRepository<PrivateTodoEntity, Long> {

    Optional<PrivateTodoEntity> findByIdAndAuthorUserId(Long id, Long authorUserId);

    Slice<PrivateTodoEntity> findAllByAuthorUserId(Long authorUserId, PageRequest pageRequest);

}
