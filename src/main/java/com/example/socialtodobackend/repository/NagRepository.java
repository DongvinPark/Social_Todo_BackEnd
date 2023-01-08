package com.example.socialtodobackend.repository;

import com.example.socialtodobackend.entity.NagEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NagRepository extends JpaRepository<NagEntity, Long> {

    Slice<NagEntity> findAllByPublishedTodoPKId(Long publishedTodoPKId, PageRequest pageRequest);

    void deleteByPublishedTodoPKIdAndNagSentUserPKId(Long publishedTodoPKId, Long nagSentUserPKId);

}
