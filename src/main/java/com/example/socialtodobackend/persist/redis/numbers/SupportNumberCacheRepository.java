package com.example.socialtodobackend.persist.redis.numbers;

import com.example.socialtodobackend.exception.SingletonException;
import com.example.socialtodobackend.utils.AWSSecretValues;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SupportNumberCacheRepository {
    private final RedisTemplate<String, String> template;

    //만료기간을 설정하지 않는다. 공개투두 아이템이 삭제될 때 같이 삭제되기 때문이다.

    //응원 숫자를 레디스로부터 가져온다.
    public Long getSupportNumber(Long publicTodoPKId){
        try {
            String key = getKey(publicTodoPKId);
            String value = template.opsForValue().get(key);
            return Long.parseLong(value);
        } catch (Exception e){
            throw SingletonException.REDIS_GET_OPERATION_FAILED;
        }
    }


    //공개 투두 아이템이 만들어졌을 때 기록을 위한 키밸류 썅을 캐싱한다.
    public void setInitialSupport(Long publicTodoPKId){
        String key = getKey(publicTodoPKId);
        template.opsForValue().set(key, "0");
    }


    //응원 숫자를 += 1 한다.
    public void plusOneSupport(Long publicTodoPKId){
        String key = getKey(publicTodoPKId);
        template.opsForValue().increment(key);
    }


    //응원 숫자를 -= 1 한다.
    public void minusOneSupport(Long publicTodoPKId){
        String key = getKey(publicTodoPKId);
        template.opsForValue().decrement(key);
    }


    //응원 숫자를 기록한 키-밸류 쌍을 삭제한다.
    public void deleteSupportNumberInfo(Long publicTodoPKId){
        String key = getKey(publicTodoPKId);
        template.delete(key);
    }


    private String getKey(Long publicTodoPKId){
        return AWSSecretValues.REDIS_KEY_PREFIX_SUPPORT_NUMBER + publicTodoPKId;
    }
}
