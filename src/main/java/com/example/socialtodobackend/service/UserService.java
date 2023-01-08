package com.example.socialtodobackend.service;

import com.example.socialtodobackend.dto.UserDto;
import com.example.socialtodobackend.dto.publictodo.PublicTodoDto;
import com.example.socialtodobackend.entity.FollowEntity;
import com.example.socialtodobackend.exception.SocialTodoException;
import com.example.socialtodobackend.repository.FollowRepository;
import com.example.socialtodobackend.repository.PublicTodoRepository;
import com.example.socialtodobackend.repository.UserRepository;
import com.example.socialtodobackend.type.ErrorCode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final PublicTodoRepository publicTodoRepository;



    /**
     * 특정 유저의 타임라인 화면을 구성한다.
     * <br><br/>
     * 타임라인 화면을 구성하는 불변규칙은 다음과 같다.
     * "특정 유저가 팔로우한 모든 다른 유저들이 올려 놓은 공개 투두 아이템들 중에서 마감기한이 오늘에 해당하면서 아직 완료되지 않은 공개투두 아이템들"
     * 을 구성해서 리턴하면 된다.
     * <br><br/>
     * 타임라인은 유저 정보와 공개 투두 아이템 정보 모두를 레디스 캐싱하고 배치 처리까지 돌려야되는 고난도 작업이다.
     * 현재는 비효율적이겠지만 일단 구현하고나서 나중에 배포한 후에 레디스와 배치를 동원해서 최적화시킨다.
     * */
    public List<PublicTodoDto> makeTimeLine(Long userPKId) {
        // userPKId 번호를 주키고 가지고 있는 유저가 팔로우를 한 다른 모든 유저들의 주키 아이디 번호를 찾아낸다.
        List<Long> followeeUserPKIdList = followRepository.findAllByFollowSentUserId(userPKId).stream().map(FollowEntity::getFollowReceivedUserId).collect(
            Collectors.toList());

        // 위에서 찾아낸 followeeUserPKIdList 를
        // findAllByFinishedEqualsAndDeadlineDateEqualsAndAuthorUserIdIn() 메서드에 전달하여
        // 오늘이 마감기한인데, 아직 왼료되지 않은 공개 투두 아이템 중에서
        // 작성자 주키 아이디가 followeeUserPKIdList 에 들어 있는 공개 투두 아이템을 publicTodoDto로 만들어서
        // 최종 리턴한다.
        return publicTodoRepository.findAllByFinishedIsFalseAndDeadlineDateEqualsAndAuthorUserIdIn(
            LocalDate.now(), followeeUserPKIdList
        ).stream().map(PublicTodoDto::fromEntity).collect(Collectors.toList());
    }



    /**
     * 유저의 닉네임을 검색한다.
     * 닉네임은 영소문자 또는 숫자로만 구성돼 있으므로 이를 어긴 검색어에 대해서는 예외를 던진다.
     * 페이징이 필요하다.
     * */
    public List<UserDto> searchUsersByNickname(String userNickname) {
        validateNicknameSearchWordString(userNickname);

        return userRepository.findAllByNicknameContaining(userNickname).stream()
            .map(UserDto::fromEntity).collect(Collectors.toList());
    }



    //--------------- PRIVATE HELPER METHOD AREA ------------


    private void validateNicknameSearchWordString(String input){
        for(char c : input.toCharArray()){
            if(!Character.isDigit(c) && !Character.isLowerCase(c)){
                throw new SocialTodoException(ErrorCode.INVALID_NICKNAME);
            }
        }
    }


}
