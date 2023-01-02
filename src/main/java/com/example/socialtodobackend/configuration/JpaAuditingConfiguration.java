package com.example.socialtodobackend.configuration;


import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 이 클래스는 엔티티 클래스의 createdAt, modifiedAt 필드가 생성 또는 수정될 때마다 시스템에서 자동으로 그 일시를 기록하게 해주는 Auditing 기능을 활성화 시키기 위해서 존재한다.
 * */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfiguration {

}
