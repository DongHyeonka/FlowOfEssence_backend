package com.lolclone.authentication_management_server.infrastructure.openid;

import com.lolclone.common_module.commonexception.InternalServerException;
import com.lolclone.common_module.exception.domain.ExceptionType;
import io.jsonwebtoken.io.Parser;
import io.jsonwebtoken.security.JwkSet;
import io.jsonwebtoken.security.Jwks;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Slf4j
@Component
public class KakaoOpenIdJwksClient {

    private final RestTemplate restTemplate;
    private final Parser<JwkSet> parser;

    public KakaoOpenIdJwksClient(
        RestTemplateBuilder restTemplateBuilder
    ) {
        this.restTemplate = restTemplateBuilder
            .errorHandler(new KakaoOpenIdJwksErrorHandler())
            .setConnectTimeout(Duration.ofSeconds(2))
            .setReadTimeout(Duration.ofSeconds(3))
            .build();
        this.parser = Jwks.setParser()
            .build();
    }

    public JwkSet requestGetJwks() {
        try {
            String jsonKeys = restTemplate.getForObject("https://kauth.kakao.com/.well-known/jwks.json", String.class);
            log.info("Kakao JWKS 공개키 목록을 조회했습니다.");
            return parser.parse(jsonKeys);
        } catch (Exception e) {
            log.warn("Kakao JWKS 서버가 응답하지 않습니다.");
            throw new InternalServerException(ExceptionType.OPEN_ID_PROVIDER_NOT_RESPONSE);
        }
    }
}
