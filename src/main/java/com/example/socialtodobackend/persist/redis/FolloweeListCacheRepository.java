package com.example.socialtodobackend.persist.redis;

import com.example.socialtodobackend.exception.SingletonException;
import com.example.socialtodobackend.utils.AWSSecretValues;
import com.example.socialtodobackend.utils.CommonUtils;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FolloweeListCacheRepository {

    private final RedisTemplate<String, String> template;

    //이것은 값이 큰 편이므로 캐시에서 저장되는 유효기간을 짧게 설정한다.
    private static final Duration LIST_DURATION = Duration.ofDays(CommonUtils.USER_FOLLOWEE_LIST_VALID_DAY_LENGTH);


    //레디스 내에 특정 유저가 팔로우 하는 다른 유저들의 주키 아이디 값 리스트를 캐싱해 둔 것이 있는지 확인한다.
    public boolean isFolloweeListCacheHit(Long userPKId){
        String key = getKey(userPKId);
        return Boolean.TRUE.equals(template.hasKey(key));
    }


    //DB로부터 특정 유저가 팔로우한 사람들의 주키 아이디 값 리스트를 받아서 레디스에 저장한다.
    public void setFolloweeList(List<Long> pkIdList, Long userPKId) {
        log.info("팔로이 리스트 셋팅 진입");
        String key = getKey(userPKId);

        template.expire(key, LIST_DURATION);

        for(Long id : pkIdList){
            log.info("레디스 리스트 값 삽입 : " + id);
            template.opsForList().rightPush(key, String.valueOf(id));
        }
        log.info("레디스에 팔로이 리스트 캐시 완료.");
    }


    //새로운 팔로우관계가 발생했을 경우, 처리해준다.
    public void addNewFollowee(Long userPKId, Long followeePKId){
        String key = getKey(userPKId);
        template.opsForList().rightPush(key, String.valueOf(followeePKId));
    }


    //레디스로부터 특정 유저가 팔로우한 사람들의 리스트를 가져온다.
    public List<Long> getFolloweeList(Long userPKId){
        log.info("레디스 리스트 겟 진입");
        String key = getKey(userPKId);

        try {
            long size = template.opsForList().size(key);

            List<String> listFromRedis = template.opsForList().range(key, 0, size);

            log.info("레디스로부터 리스트 가져오기 완료. Expire 설정은 나중에한다.");

            log.info("레디스로부터 팔로우 리스트 리턴 완료.");
            return listFromRedis.stream().map(Long::valueOf).collect(Collectors.toList());
        } catch (Exception e){
            throw SingletonException.REDIS_GET_OPERATION_FAILED;
        }
    }


    private String getKey(Long userPKId){
        return AWSSecretValues.REDIS_KEY_PREFIX_FOLLOWEE_LIST + userPKId;
    }
}
