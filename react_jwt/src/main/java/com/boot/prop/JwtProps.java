package com.boot.prop;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Component
@ConfigurationProperties("com.boot") //com.boot 경로 하위 속성들로 지정
public class JwtProps {
	//com.boot.secret-key로 지정된 값을 주입받는 필드
	private String secretKey;
}