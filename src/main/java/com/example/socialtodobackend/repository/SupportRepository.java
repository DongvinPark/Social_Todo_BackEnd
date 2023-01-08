package com.example.socialtodobackend.repository;

import com.example.socialtodobackend.entity.SupportEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportRepository extends JpaRepository<SupportEntity, Long> {

    List<SupportEntity> findAllByPublishedTodoPKId(Long publishedTodoPKId);

    void deleteByPublishedTodoPKIdAndSupportSentUserPKId(Long publishedTodoPKId,
        Long supportSentUserPKId);

}
