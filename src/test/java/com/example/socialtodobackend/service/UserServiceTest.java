package com.example.socialtodobackend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.socialtodobackend.dto.publictodo.PublicTodoDto;
import com.example.socialtodobackend.dto.user.UserDto;
import com.example.socialtodobackend.dto.user.UserSignInRequestDto;
import com.example.socialtodobackend.dto.user.UserSignInResponseDto;
import com.example.socialtodobackend.dto.user.UserSignUpRequestDto;
import com.example.socialtodobackend.exception.SocialTodoException;
import com.example.socialtodobackend.persist.FollowEntity;
import com.example.socialtodobackend.persist.FollowRepository;
import com.example.socialtodobackend.persist.PublicTodoEntity;
import com.example.socialtodobackend.persist.PublicTodoRepository;
import com.example.socialtodobackend.persist.UserEntity;
import com.example.socialtodobackend.persist.UserRepository;
import com.example.socialtodobackend.persist.redis.FolloweeListCacheRepository;
import com.example.socialtodobackend.persist.redis.JwtCacheRepository;
import com.example.socialtodobackend.persist.redis.numbers.NagNumberCacheRepository;
import com.example.socialtodobackend.persist.redis.numbers.SupportNumberCacheRepository;
import com.example.socialtodobackend.security.JWTProvider;
import com.example.socialtodobackend.type.ErrorCode;
import com.example.socialtodobackend.utils.CommonUtils;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FollowRepository followRepository;

    @Mock
    private PublicTodoRepository publicTodoRepository;

    @Mock
    private JWTProvider jwtProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SupportNumberCacheRepository supportNumberCacheRepository;

    @Mock
    private NagNumberCacheRepository nagNumberCacheRepository;

    @Mock
    private JwtCacheRepository jwtCacheRepository;

    @Mock
    private FolloweeListCacheRepository followeeListCacheRepository;

    @InjectMocks
    private UserService userService;




    @Test
    @DisplayName("회원가입 요청")
    void success_RegisterUser(){
        //given
        given(passwordEncoder.encode(anyString())).willReturn("abc");

        UserSignUpRequestDto userSignUpRequestDto = UserSignUpRequestDto.builder()
            .emailAddr("e1@mail.com")
            .nickname("one")
            .password("1111")
            .build();

        UserEntity userEntity = UserEntity.builder()
            .id(1L)
            .emailAddr("e1@mail.com")
            .password("abc")
            .nickname("one")
            .build();

        given(userRepository.existsByEmailAddr(anyString())).willReturn(false);

        given(userRepository.save(any())).willReturn(userEntity);

        //when
        UserDto userDto = userService.registerUser(userSignUpRequestDto);

        //then
        assertEquals(1L, userDto.getId());
        assertEquals("one", userDto.getNickname());
    }




    @Test
    @DisplayName("회원가입 실패 - 닉네임이 빈 문자열")
    void failed_RegisterUser_NicknameIsEmptyString(){
        //given
        UserSignUpRequestDto userSignUpRequestDto = UserSignUpRequestDto.builder()
            .password("1111")
            .nickname("")
            .emailAddr("e1@mail.com")
            .build();

        //when
        SocialTodoException exception = assertThrows(
            SocialTodoException.class ,
            () -> userService.registerUser(userSignUpRequestDto)
        );

        //then
        assertEquals(ErrorCode.INVALID_NICKNAME, exception.getErrorCode());
    }




    @Test
    @DisplayName("회원가입 실패 - 닉네임에 영소문자 또는 숫자 이외의 문자 포함")
    void failed_RegisterUser_NicknameContainingInvalidChar(){
        //given
        UserSignUpRequestDto userSignUpRequestDto = UserSignUpRequestDto.builder()
            .password("1111")
            .nickname("nyanCat ^-^")
            .emailAddr("e1@mail.com")
            .build();

        //when
        SocialTodoException exception = assertThrows(
            SocialTodoException.class ,
            () -> userService.registerUser(userSignUpRequestDto)
        );

        //then
        assertEquals(ErrorCode.INVALID_NICKNAME, exception.getErrorCode());
    }




    @Test
    @DisplayName("회원가입 실패 - 이미 존재하는 닉네임")
    void failed_RegisterUser_NicknameAlreadyExists(){
        //given
        UserSignUpRequestDto userSignUpRequestDto = UserSignUpRequestDto.builder()
            .password("1111")
            .nickname("one")
            .emailAddr("e1@mail.com")
            .build();

        given(userRepository.existsByNickname(anyString())).willReturn(true);

        //when
        SocialTodoException exception = assertThrows(
            SocialTodoException.class ,
            () -> userService.registerUser(userSignUpRequestDto)
        );

        //then
        assertEquals(ErrorCode.NICKNAME_ALREADY_EXISTS, exception.getErrorCode());
    }




    @Test
    @DisplayName("회원가입 실패 - 이미 존재하는 이메일 주소")
    void failed_RegisterUser_EmailAddrAlreadyExists(){
        //given
        UserSignUpRequestDto userSignUpRequestDto = UserSignUpRequestDto.builder()
            .password("1111")
            .nickname("one")
            .emailAddr("e1@mail.com")
            .build();

        given(userRepository.existsByEmailAddr(anyString())).willReturn(true);

        //when
        SocialTodoException exception = assertThrows(
            SocialTodoException.class ,
            () -> userService.registerUser(userSignUpRequestDto)
        );

        //then
        assertEquals(ErrorCode.EMAIL_ADDRESS_ALREADY_EXISTS, exception.getErrorCode());
    }




    @Test
    @DisplayName("유저 로그인 요청")
    void success_AuthenticateUser(){
        //given
        UserSignInRequestDto userSignInRequestDto = UserSignInRequestDto.builder()
            .email("e1@mail.com")
            .password("1111")
            .build();

        UserEntity userEntity = UserEntity.builder()
            .id(1L)
            .nickname("one")
            .password("1111")
            .emailAddr("e1@mail.com")
            .build();

        given(jwtProvider.create(any())).willReturn("abc");

        given(userRepository.findByEmailAddr(anyString())).willReturn(Optional.of(userEntity));

        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);

        doNothing().when(jwtCacheRepository).setJwtAtRedis(anyString(), anyLong());

        //when
        UserSignInResponseDto userSignInResponseDto = userService.authenticateUser(userSignInRequestDto);

        //then
        assertEquals(1L, userSignInResponseDto.getId());
        assertEquals("e1@mail.com", userSignInResponseDto.getEmailAddr());
        assertEquals("one", userSignInResponseDto.getNickname());
        assertEquals("abc", userSignInResponseDto.getJwt());
    }




    @Test
    @DisplayName("유저 로그인 요청 실패 - 이메일 주소 불일치")
    void failed_AuthenticateUser_EmailAddrNotMatch(){
        //given
        UserSignInRequestDto userSignInRequestDto = UserSignInRequestDto.builder()
            .email("e1@mail.com")
            .password("1111")
            .build();

        given(userRepository.findByEmailAddr(anyString())).willReturn(Optional.empty());

        //when
        SocialTodoException exception = assertThrows(
            SocialTodoException.class ,
            () -> userService.authenticateUser(userSignInRequestDto)
        );

        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }




    @Test
    @DisplayName("유저 로그인 요청 실패 - 비밀번호 불일치")
    void failed_AuthenticateUser_PasswordNotMatch(){
        //given
        UserSignInRequestDto userSignInRequestDto = UserSignInRequestDto.builder()
            .email("e1@mail.com")
            .password("2222")
            .build();

        UserEntity userEntity = UserEntity.builder()
            .id(1L)
            .nickname("one")
            .password("1111")
            .emailAddr("e1@mail.com")
            .build();

        given(userRepository.findByEmailAddr(anyString())).willReturn(Optional.of(userEntity));

        given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

        //when
        SocialTodoException exception = assertThrows(
            SocialTodoException.class ,
            () -> userService.authenticateUser(userSignInRequestDto)
        );

        //then
        assertEquals(ErrorCode.PASSWORD_NOT_MATCH, exception.getErrorCode());
    }




    @Test
    @DisplayName("타임라인 요청")
    void success_MakeTimeLine(){
        //given
        FollowEntity followEntity = FollowEntity.builder()
            .followSentUserId(1L)
            .followReceivedUserId(2L)
            .build();

        List<FollowEntity> followEntityList = new ArrayList<>();
        followEntityList.add(followEntity);

        PublicTodoEntity publicTodoEntity = PublicTodoEntity.builder()
            .id(1L)
            .authorUserId(2L)
            .deadlineDate(LocalDate.now())
            .finished(false)
            .build();

        List<PublicTodoEntity> publicTodoEntityList = new ArrayList<>();
        publicTodoEntityList.add(publicTodoEntity);
        Slice<PublicTodoEntity> todoSlice = new PageImpl<>(publicTodoEntityList);

        given(followeeListCacheRepository.isFolloweeListCacheHit(1L)).willReturn(false);

        given(
            followRepository.findAllByFollowSentUserId(anyLong())
        ).willReturn(followEntityList);

        given(
            publicTodoRepository.findAllByFinishedIsFalseAndDeadlineDateEqualsAndAuthorUserIdIn(
                LocalDate.now(),
                followEntityList.stream().map(FollowEntity::getFollowReceivedUserId).collect(Collectors.toList()),
                PageRequest.of(0, CommonUtils.PAGE_SIZE)
            )
        ).willReturn(todoSlice);

        given(supportNumberCacheRepository.getSupportNumber(1L)).willReturn(0L);

        given(nagNumberCacheRepository.getNagNumber(1L)).willReturn(0L);

        //when
        List<PublicTodoDto> publicTodoDtoList = userService.makeTimeLine(1L, PageRequest.of(0, CommonUtils.PAGE_SIZE));

        //then
        assertEquals(1, publicTodoDtoList.size());
    }




    @Test
    @DisplayName("유저 검색 요청")
    void success_SearchUsersByNickname(){
        //given
        UserEntity userEntity = UserEntity.builder()
            .id(1L)
            .nickname("one")
            .password("1111")
            .emailAddr("e1@mail.com")
            .build();

        List<UserEntity> userEntityList = new ArrayList<>();
        userEntityList.add(userEntity);

        Slice<UserEntity> slice = new PageImpl<>(userEntityList);

        given(userRepository.findAllByNicknameContaining(
            "one", PageRequest.of(0, CommonUtils.PAGE_SIZE)
        )).willReturn(slice);

        //when
        List<UserDto> userDtoList = userService.searchUsersByNickname("one", PageRequest.of(0, CommonUtils.PAGE_SIZE));

        //then
        assertEquals(1, userDtoList.size());
    }




    @Test
    @DisplayName("유저 상태 메시지 수정")
    void success_UpdateUserStatusMessage(){
        //given
        UserEntity user = UserEntity.builder()
            .id(1L)
            .statusMessage("변경 메시지")
            .build();

        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        //when
        userService.updateUserStatusMessage(1L, "변경 메시지");

        //then
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any());
    }




    @Test
    @DisplayName("유저 상태 메시지 수정 실패 - 대상 유저가 없음")
    void failed_UpdateUserStatusMessage_UserNotFound(){
        //given
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        //when
        SocialTodoException exception = assertThrows(
            SocialTodoException.class ,
            () -> userService.updateUserStatusMessage(1L, "I am player one.")
        );

        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }




    @Test
    @DisplayName("유저 상태 메시지 수정 실패 - 상태 메시지 한계 길이 초과")
    void failed_UpdateUserStatusMessage_StatusMessageTooLong(){
        //given
        UserEntity userEntity = UserEntity.builder()
            .id(1L)
            .nickname("one")
            .password("1111")
            .emailAddr("e1@mail.com")
            .build();

        String tooLongString = "000000000_000000000_000000000_000000000_000000000_000000000_000000000_";

        given(userRepository.findById(anyLong())).willReturn(Optional.of(userEntity));

        //when
        SocialTodoException exception = assertThrows(
            SocialTodoException.class ,
            () -> userService.updateUserStatusMessage(1L, tooLongString)
        );

        //then
        assertEquals(ErrorCode.STATUS_MESSAGE_TOO_LONG, exception.getErrorCode());
    }


}








