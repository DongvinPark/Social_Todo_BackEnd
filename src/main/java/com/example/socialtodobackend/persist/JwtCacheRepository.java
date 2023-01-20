package com.example.socialtodobackend.persist.redis;

import com.example.socialtodobackend.utils.CommonUtils;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JwtCacheRepository {
    //AWS ElasticCache for Redis에서 제공하는 기능들을 스프링 부트 앱 내에서
    //간편하게 호출 할 수 있게 만들어주는 클래스다.

    private final RedisTemplate<String, String> template;

    //키의 만료 기간을 설정해준다.
    private static final Duration JWT_DURATION = Duration.ofDays(CommonUtils.JWT_VALID_DAY_LENGTH);


    public void setJwtAtRedis(String jwt, Long userPKId) {
        String key = getKey(userPKId);
        template.opsForValue().set(key, jwt, JWT_DURATION);
        log.info("레디스에 유저 jwt 셋팅 완료.");
    }


    public String getJwtFromRedis(Long userPKId){
        String key = getKey(userPKId);
        String jwt = template.opsForValue().get(key);
        log.info("레디스로부터 유저 키 가져오기 완료.");
        return jwt;
    }



    private String getKey(Long userPKId){
        return "USER:JWT:" + userPKId;
    }


}