package com.example.socialtodobackend.configuration;

import com.example.socialtodobackend.security.JWTAuthenticationFilter;
import com.example.socialtodobackend.security.JWTProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
@Slf4j
public class WebSecurityConfig {

    private final JWTProvider jwtProvider;


    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    //WebSecurityConfigurerAdapter deprecated 되었으므로 오버라이드 하지 않는다.
    //대산 필터체인 설정 메서드를 빈으로 등록해준다.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
            //log.info("필터체인 메서드 진입");
        httpSecurity
            .cors()
            .and()
            .csrf().disable()
            .httpBasic().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //토큰 기반 인증을 하므로 세션을 '상태가 없도록' 해준다.
            .and()
            .authorizeRequests()
                .antMatchers("/sign-up", "/sign-in").permitAll()//회원가입과 로그인은 인증이 없어야 한다.
            .anyRequest()
            .authenticated();

        //log.info("httpSecurity 속성 설정 완료. 필터 추가 작업 시작");

        //JWTAuthenticationFilter가 Cors 필터 다음에 동작하도록 설정해준다.
        //cors는 추후에 프런트엔드와 통합할 때 등장하는 개념이다.
        httpSecurity.addFilterAfter(new JWTAuthenticationFilter(jwtProvider), CorsFilter.class);

        //log.info("httpSecurity 필터 설정 완료.");

        return httpSecurity.build();
    }

}
