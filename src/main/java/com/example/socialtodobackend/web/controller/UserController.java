package com.example.socialtodobackend.web.controller;

import com.example.socialtodobackend.dto.APIDataResponse;
import com.example.socialtodobackend.dto.publictodo.PublicTodoDto;
import com.example.socialtodobackend.dto.user.UserDto;
import com.example.socialtodobackend.dto.user.UserSignInRequestDto;
import com.example.socialtodobackend.dto.user.UserSignInResponseDto;
import com.example.socialtodobackend.dto.user.UserSignUpRequestDto;
import com.example.socialtodobackend.service.UserService;
import com.example.socialtodobackend.utils.CommonUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;



    @PostMapping("/sign-up")
    public APIDataResponse<UserDto> signUp(
        @RequestBody UserSignUpRequestDto userDto
    ){
        return APIDataResponse.of(userService.registerUser(userDto));
    }




    @PostMapping("/sign-in")
    public APIDataResponse<UserSignInResponseDto> authenticate(
        @RequestBody UserSignInRequestDto signInRequestDto
    ){
        /**
         * 유저가 로그인에 성공했을 경우, 그 레디스 캐시에 {유저 주키 : 그 유저가 팔로우 하고 있는 다른 유저들의 주키 아이디 리스틑}
         * 를 캐시해 둬야 한다. 유효기간은 JWT의 유효기간인 CommonUtils.JWT_VALID_DAY_LENGTH 의 값과 동일하게 혹은 더 짧게 설정한다.
         * */
        return APIDataResponse.of(userService.authenticateUser(signInRequestDto));
    }




    @PutMapping("/update/status-message")
    public void updateStatusMessage(
        @AuthenticationPrincipal Long userPKId, @RequestParam String statusMessage
    ){
        userService.updateUserStatusMessage(userPKId, statusMessage);
    }




    @GetMapping("/time-line")
    public APIDataResponse< List<PublicTodoDto> > getTimeLine(
        @AuthenticationPrincipal Long userPKId, @RequestParam int pageNumber
    ){
        /**
         * 레디스 캐시 서버로부터 {유저 주키 : 해당 유저가 팔로우한 다른 유저들의 주키 아이디 리스트} 키-밸류 썅을 찾아내서
         * 타임라인 구성을 위한 쿼리에 사용해야 한다.
         * 또한 쿼리로 구성된 엔티티 하나하나 마다 레디스 서버에서 {투두 주키 : 응원수} 와 {투두 주키 : 잔소리수} 키-밸류 쌍을 찾아내서
         * PublicTodoDto 구성에 사용해야 한다.
         *
         * 이때 레디스에서 {투두 주키 : 응원수} 와 {투두 주키 : 잔소리수} 키-밸류 쌍을 찾지 못한 경우 PublicTodoDto의 응원/잔소리 숫자 모두를
         * 0L로 설정하면 된다.
         * */
        PageRequest pageRequest = PageRequest.of(pageNumber, CommonUtils.PAGE_SIZE);
        return APIDataResponse.of(userService.makeTimeLine(userPKId, pageRequest));
    }




    /**
     * 회원가입 후 정상적으로 로그인 했다면, 누구나 검색 기능을 사용할 수 있으므로
     * @AuthenticationPrincipal 을 사용하면 안 된다.
     * */
    @GetMapping("/search/users")
    public APIDataResponse< List<UserDto> > searchUsers(
        @RequestParam String userNickname,
        @RequestParam int pageNumber
    ){
        PageRequest pageRequest = PageRequest.of(pageNumber, CommonUtils.PAGE_SIZE);
        return APIDataResponse.of(userService.searchUsersByNickname(userNickname, pageRequest));
    }

}
