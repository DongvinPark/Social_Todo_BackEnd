package com.example.socialtodobackend.repository;

import com.example.socialtodobackend.entity.SupportEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportRepository extends JpaRepository<SupportEntity, Long> {

    Slice<SupportEntity> findAllByPublishedTodoPKId(Long publishedTodoPKId, PageRequest pageRequest);

    void deleteByPublishedTodoPKIdAndSupportSentUserPKId(Long publishedTodoPKId,
        Long supportSentUserPKId);

}