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
