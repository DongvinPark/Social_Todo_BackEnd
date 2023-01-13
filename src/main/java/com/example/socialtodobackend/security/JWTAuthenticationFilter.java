package com.example.socialtodobackend.security;

import com.example.socialtodobackend.exception.SingletonException;
import com.example.socialtodobackend.persist.UserRepository;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTProvider jwtProvider;
    private final UserRepository userRepository;


    /**
     * 스프링 시큐리티 필터 체인에서 실시할 필터링 내용을 정의해준다.
     * */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChan) throws ServletException, IOException {
        try {
            //프런트엔드 클라이언트 측에서 'bearer token'에 담아서 보낸 JWT를 get한다.
            String token = parseBearerToken(request);

            if(token != null && !token.equalsIgnoreCase("null")) {

                Long userId = jwtProvider.validateAndGetUserPKId(token);

                //log.info("인증 완료 된 사용자의 주키 아이디 값 : " + userId);

                //JWT에서 얻은 유저의 주키 아이디가 실존하는지를 판단한다.
                //나중에 레디스로 캐시할 때는 DB 보다 레디스 클러스터를 먼저 보도록 코드를 수정한다.
                if(userRepository.existsById(userId)){
                    //이 부분을 통해서 컨트롤러 계층 메서드들의 파라미터들 중에서 @AuthenticationPrincipal 이 붙어 있는
                    //매개변수들에게 값을 넘겨줄 수 있다.
                    AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        AuthorityUtils.NO_AUTHORITIES
                    );

                    authentication.setDetails( new WebAuthenticationDetailsSource().buildDetails(request) );

                    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

                    securityContext.setAuthentication(authentication);

                    SecurityContextHolder.setContext(securityContext);
                } else {
                    throw SingletonException.USER_NOT_FOUND;
                }
            }//if
        }//try
        catch(Exception e) {
            log.error("Could not set user authentication in security context", e);
        }//catch

        filterChan.doFilter(request, response);
    }



    private String parseBearerToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if( StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ") ) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
