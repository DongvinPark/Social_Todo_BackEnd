package com.example.socialtodobackend.repository;

import com.example.socialtodobackend.entity.NagEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NagRepository extends JpaRepository<NagEntity, Long> {

    List<NagEntity> findAllByPublishedTodoPKId(Long publishedTodoPKId);

    void deleteByPublishedTodoPKIdAndNagSentUserPKId(Long publishedTodoPKId, Long nagSentUserPKId);

}
