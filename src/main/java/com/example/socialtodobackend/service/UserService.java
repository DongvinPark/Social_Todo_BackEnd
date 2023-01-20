package com.example.socialtodobackend.service;

import com.example.socialtodobackend.dto.publictodo.PublicTodoDto;
import com.example.socialtodobackend.dto.user.UserDto;
import com.example.socialtodobackend.dto.user.UserSignInRequestDto;
import com.example.socialtodobackend.dto.user.UserSignInResponseDto;
import com.example.socialtodobackend.dto.user.UserSignUpRequestDto;
import com.example.socialtodobackend.persist.FollowEntity;
import com.example.socialtodobackend.persist.UserEntity;
import com.example.socialtodobackend.exception.SingletonException;
import com.example.socialtodobackend.persist.FollowRepository;
import com.example.socialtodobackend.persist.PublicTodoRepository;
import com.example.socialtodobackend.persist.UserRepository;
import com.example.socialtodobackend.persist.redis.JwtCacheRepository;
import com.example.socialtodobackend.security.JWTProvider;
import com.example.socialtodobackend.utils.CommonUtils;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final PublicTodoRepository publicTodoRepository;
    private final JWTProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final JwtCacheRepository jwtCacheRepository;



    /**
     * 특정 유저의 회원가입을 처리한다.
     * JWT는 이때 전달하는 것이 아니라, 로그인을 성공했을 때만 전달해야 한다.
     * <br><br/>
     * 닉네임의 유효성(영소문자와 숫자로만 구성)과 중복성 검사, 그리고 이메일 주소 중복 검사를 실시한다.
     * */
    @Transactional
    public UserDto registerUser(UserSignUpRequestDto userSignUpRequestDto) {

        validateNicknameAndEmailAddress(userSignUpRequestDto.getNickname(), userSignUpRequestDto.getEmailAddr());

        UserEntity userEntity = userRepository.save(
            UserEntity.builder()
                .emailAddr(userSignUpRequestDto.getEmailAddr())
                .password(passwordEncoder.encode(userSignUpRequestDto.getPassword()))
                .nickname(userSignUpRequestDto.getNickname())
                .build()
        );
        return UserDto.fromEntity(userEntity);
    }



    @Transactional(readOnly = true)
    public UserSignInResponseDto authenticateUser(UserSignInRequestDto signInRequestDto) {
        UserEntity originalUserEntity = getByCredentials(
            signInRequestDto.getEmail(),
            signInRequestDto.getPassword(),
            passwordEncoder
        );

        //getByCredentials() 메서드를 통과했다면, 유저 엔티티를 찾아내서 로그인 성공 처리를 진행 할 수 있다.
        String jwt = jwtProvider.create(originalUserEntity);

        Long userPKId = jwtProvider.validateAndGetUserPKId(jwt);

        //유저의 JWT를 레디스에 캐시해 둔다.
        jwtCacheRepository.setJwtAtRedis(jwt, userPKId);

        return UserSignInResponseDto.fromEntity(originalUserEntity, jwt);
    }



    /**
     * 특정 유저의 타임라인 화면을 구성한다.
     * <br><br/>
     * 타임라인 화면을 구성하는 불변규칙은 다음과 같다.
     * "특정 유저가 팔로우한 모든 다른 유저들이 올려 놓은 공개 투두 아이템들 중에서 마감기한이 오늘에 해당하면서 아직 완료되지 않은 공개투두 아이템들"
     * 을 구성해서 리턴하면 된다.
     * <br><br/>
     * 타임라인을 캐싱해야 할지 말아야 할지는 별도로 테스트 해본 후에 판단한다.
     * */
    @Transactional(readOnly = true)
    public List<PublicTodoDto> makeTimeLine(Long userPKId, PageRequest pageRequest) {
        // userPKId 번호를 주키로 가지고 있는 유저가 팔로우를 한 다른 모든 유저들의 주키 아이디 번호를 찾아낸다.
        // 이 숫자는 5000을 초과할 수 없으므로, 일단 전부 담아 둔다.
        //페이징을 사용하지 않는 버전의 메서드를 호출한다.
        List<Long> followeeUserPKIdList = followRepository.findAllByFollowSentUserId(userPKId).stream().map(FollowEntity::getFollowReceivedUserId).collect(Collectors.toList());

        // 위에서 찾아낸 followeeUserPKIdList 를
        // findAllByFinishedEqualsAndDeadlineDateEqualsAndAuthorUserIdIn() 메서드에 전달하여
        // 오늘이 마감기한인데, 아직 왼료되지 않은 공개 투두 아이템 중에서
        // 작성자 주키 아이디가 followeeUserPKIdList 에 들어 있는 공개 투두 아이템을 publicTodoDto로 만들어서
        // 최종 리턴한다.
        // 아래의 쿼리를 실행한 후, 레디스서버 로부터 각 공개 투두 아이템마다 응원/좋아요 숫자를 읽어와서 dto에 붙여줘야 한다.
        return publicTodoRepository.findAllByFinishedIsFalseAndDeadlineDateEqualsAndAuthorUserIdIn(
            LocalDate.now(), followeeUserPKIdList, pageRequest
        ).getContent().stream().map(PublicTodoDto::fromEntity).collect(Collectors.toList());
    }



    /**
     * 유저의 닉네임을 검색한다.
     * 닉네임은 영소문자 또는 숫자로만 구성돼 있으므로 이를 어긴 검색어에 대해서는 예외를 던진다.
     * 페이징이 필요하다.
     * */
    @Transactional(readOnly = true)
    public List<UserDto> searchUsersByNickname(String userNickname, PageRequest pageRequest) {
        validateNicknameString(userNickname);

        return userRepository.findAllByNicknameContaining(userNickname, pageRequest).getContent().stream().map(UserDto::fromEntity).collect(Collectors.toList());
    }



    /**
     * 유저의 상태 메시지를 수정한다.
     * */
    @Transactional
    public void updateUserStatusMessage(Long userPKId, String statusMessage) {
        //수정할 때 불러올 엔티티가 필요하기 때문에 이 부분을 삭제할 수는 없다.
        UserEntity userEntity = userRepository.findById(userPKId).orElseThrow(
            () -> SingletonException.USER_NOT_FOUND
        );

        if(statusMessage.length() > CommonUtils.STATUS_MESSAGE_LENGTH_LIMIT){
            throw SingletonException.STATUS_MESSAGE_TOO_LONG;
        }

        userEntity.setStatusMessage(statusMessage);
        userRepository.save(userEntity);
    }



    //--------------- PRIVATE HELPER METHOD AREA ------------


    /**
     * 닉네임에 영소문자 또는 숫자의 조합으로 이루어져 있는지,
     * 새로운 닉네임인지,
     * 새로운 이메일 주소인지를 회원가입 당시에 검사한다.
     * */
    private void validateNicknameAndEmailAddress(String nickName, String emailAddr) {
        validateNicknameString(nickName);
        if(userRepository.existsByNickname(nickName)){
            throw SingletonException.NICKNAME_ALREADY_EXISTS;
        }
        if(userRepository.existsByEmailAddr(emailAddr)){
            throw SingletonException.EMAIL_ADDRESS_ALREADY_EXISTS;
        }
    }




    /**
     * 영소문자도 아니고 숫자도 아닌 문자가 닉네임에 섞여 있다면 예외를 던진다.
     * */
    private void validateNicknameString(String input){
        if(input==null || input.equals("")){
            throw SingletonException.INVALID_NICKNAME;
        }
        for(char c : input.toCharArray()){
            if(Character.isDigit(c) || Character.isLowerCase(c)){
                continue;
            }
            else{
                throw SingletonException.INVALID_NICKNAME;
            }
        }
    }



    /**
     * 입력된 이메일 및 비번을 바탕으로 로그인 성공 여부를 판별하는 것에 사용되는 메서드다.
     * 한 명의 유저의 입장에서는 Jwt를 발급 받는 순간(==로그인 하는 순간)에 딱 1번만 실행된다.
     * 그 유저의 jwt가 유효할 때는 다시 jwt가 만료되지 않는 이상 다시 실행되지 않는다.
     * */
    private UserEntity getByCredentials(
        String userEmail, String password, PasswordEncoder passwordEncoder
    ){
        UserEntity originalUserEntity = userRepository.findByEmailAddr(userEmail).orElseThrow(
            () -> SingletonException.USER_NOT_FOUND
        );

        if(passwordEncoder.matches(password, originalUserEntity.getPassword())){
            return originalUserEntity;
        }
        else{
            throw SingletonException.PASSWORD_NOT_MATCH;
        }
    }

}
