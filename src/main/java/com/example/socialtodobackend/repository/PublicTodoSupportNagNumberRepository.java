package com.example.socialtodobackend.repository;

import com.example.socialtodobackend.entity.SupportNagNumberEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicTodoSupportNagNumberRepository extends JpaRepository<SupportNagNumberEntity, Long> {

    List<SupportNagNumberEntity> findAllByAuthorUserPKId(Long authorUserPKId);

}
