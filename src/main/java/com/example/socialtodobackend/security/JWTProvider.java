package com.example.socialtodobackend.security;

import com.example.socialtodobackend.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JWTProvider {

    private static final String SECRET_KEY = "aJp4e1hf4AiuEr";

    /**
     * JWT를 생성한다.
     * */
    public String create(UserEntity userEntity){
        //Jwt의 만료기한을 24시간으로 설정
        Date expiryDate = Date.from( Instant.now().plus(1, ChronoUnit.DAYS) );

        return Jwts.builder()
            .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
            .setSubject(userEntity.getId().toString()) //유저의 주키 아이디를 스트링으로 변환하여 subject에 저장한다.
            .setIssuer("Social_Todo_BackEnd")
            .setIssuedAt(new Date())
            .setExpiration(expiryDate)
            .compact();
    }



    /**
     * 입력 받은 JWT을 파싱하여 사용자의 주키 아이디 값을 추출한다.
     * */
    public Long validateAndGetUserPKId(String token){
        Claims claims = Jwts.parser()
            .setSigningKey(SECRET_KEY)
            .parseClaimsJws(token)
            .getBody();

        return Long.parseLong(claims.getSubject());
    }

}