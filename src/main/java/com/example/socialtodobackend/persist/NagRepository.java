package com.example.socialtodobackend.persist;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NagRepository extends JpaRepository<NagEntity, Long> {

    Slice<NagEntity> findAllByPublishedTodoPKId(Long publishedTodoPKId, PageRequest pageRequest);

    void deleteAllByPublishedTodoPKId(Long publishedTodoPKId);

    void deleteByPublishedTodoPKIdAndNagSentUserPKId(Long publishedTodoPKId, Long nagSentUserPKId);

    Long countByPublishedTodoPKId(Long publishedTodoPKId);

}
