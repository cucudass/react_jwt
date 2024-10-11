package com.boot.security.jwt.provider;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.boot.dao.UserMapper;
import com.boot.dto.CustomUser;
import com.boot.dto.UserAuth;
import com.boot.dto.Users;
import com.boot.prop.JwtProps;
import com.boot.security.constants.SecurityConstants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

/*
 * JWT 토큰 기능 제공
 * 토큰 생성
 * 토큰 해석
 * 토큰 유효성 검사
 * */
@Component
@Slf4j
public class JwtTokenProvider {
//	secret-key 조회
	@Autowired
	private JwtProps jwtProps;
	
//	SQL 조회
	@Autowired
	private UserMapper userMapper;
	
//	토큰 생성
	public String createToken(int userNo, String userId, List<String> roles) {
		byte[] signingKey = getSigningKey();
		
//		JWT 토큰 생성
		String jwt = Jwts.builder()
			.signWith(Keys.hmacShaKeyFor(signingKey), Jwts.SIG.HS512)	//서명에 사용할 키와 알고리즘 설정(64byte=512bit)
			.header().add("typ", SecurityConstants.TOKEN_TYPE)			//헤더 설정
			.and()
			.expiration(new Date(System.currentTimeMillis() + 86400000))//토큰 만료 시간 설정(10일)
			.claim("uno", ""+userNo)	//클레임 설정: 사용자 번호
			.claim("uid", userId)		//클레임 설정: 사용자 아이디
			.claim("rol", roles)		//클레임 설정: 권한
			.compact();
		
		log.info("@# jwt=>"+jwt);
		return jwt;
	}
	
//	토큰 해석
//	Authorization: Bearer+jwt
	public UsernamePasswordAuthenticationToken getAuthentication(String authHeader) {
		log.info("@# authHeader=>"+authHeader);
		if (authHeader == null || authHeader.length() == 0) {
			return null;
		}
		
		try {
//			JWT 추출
			String jwt = authHeader.replace("Bearer ", "");
			
//			JWT 파싱
			Jws<Claims> parsedToken = Jwts.parser()
										.verifyWith(getShakey()).build().parseSignedClaims(jwt);
			log.info("@# parsedToken=>"+parsedToken);
			log.info("@# uno=>"+parsedToken.getPayload().get("uno"));
			
			String userNo=null;
			if (parsedToken.getPayload().get("uno") == null) {
				log.info("@# 01");
			} else {
				log.info("@# 02");
				userNo = parsedToken.getPayload().get("uno").toString();
			}
			log.info("@# userNo=>"+userNo);
			
//			사용자 번호
			int no =(userNo == null ? 0 : Integer.parseInt(userNo));
			log.info("@# no=>"+no);
			
//			사용자 아이디
			String userId = parsedToken.getPayload().get("uid").toString();
			log.info("@# userId=>"+userId);
			
			if (userId == null || userId.length() == 0) {
				return null;
			}
			
//			사용자 권한
			Claims claims = parsedToken.getPayload();
			Object roles = claims.get("rol");
			log.info("@# roles=>"+roles);
			
			Users user = new Users();
//			사용자번호, 아이디 추가
			user.setNo(no);
			user.setUserId(userId);
			List<UserAuth> authList = ((List<?>) roles)
					.stream()
					.map(auth -> new UserAuth(userId, auth.toString())).collect(Collectors.toList());
//			권한 추가
			user.setAuthList(authList);
			
			Users userInfo = userMapper.select(no);
			log.info("@# userInfo=>"+userInfo);
			
//			userInfo 가 있으면 user객체에 이름,이메일 추가
			if (userInfo != null) {
				user.setName(userInfo.getName());
				user.setEmail(userInfo.getEmail());
			}
			
			UserDetails userDetails = new CustomUser(user);
			List<SimpleGrantedAuthority> authorities = ((List<?>) roles)
					.stream()
					.map(auth -> new SimpleGrantedAuthority((String)auth)).collect(Collectors.toList());
			
//			principal: 사용자정보객체, credentials: 비밀번호, authorities: 사용자 권한목록
			return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
		} catch (ExpiredJwtException e) {
			log.warn("Request to parse expired JWT",authHeader,e.getMessage());
		} catch (UnsupportedJwtException e) {
			log.warn("Request to parse unsupported JWT",authHeader,e.getMessage());
		} catch (MalformedJwtException e) {
			log.warn("Request to parse invalid JWT",authHeader,e.getMessage());
		} catch (IllegalArgumentException e) {
			log.warn("Request to parse empty or null JWT",authHeader,e.getMessage());
		}
		
		return null;
	}
	
//	토큰 유효성 검사
	public boolean validateToken(String jwt) {
		try {
//			JWT 파싱
			Jws<Claims> claims = Jwts.parser()
										.verifyWith(getShakey()).build().parseSignedClaims(jwt);
			log.info("@# claims=>"+claims);
			log.info("@# 토큰 만료기간=>"+claims.getPayload().getExpiration());
			
//			만료시간 vs 현재시간
//			2024-10-01 vs 2024-10-08 --> 만료: true => false
//			2024-10-31 vs 2024-10-08 --> 유효: false => true
			return !claims.getPayload().getExpiration().before(new Date());
		} catch (ExpiredJwtException e) {	//토큰 만료
			log.error("Token Expired");
			return false;
		} catch (JwtException e) {	//토큰 손상
			log.error("Token Tampered");
			return false;
		} catch (NullPointerException e) {	//토큰 없음
			log.error("Token is null");
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	
//	HMAC-SHA algorithms
	private SecretKey getShakey() {
		return Keys.hmacShaKeyFor(getSigningKey());
	}
	
//	secretKey 를 조회
	public byte[] getSigningKey() {
		return jwtProps.getSecretKey().getBytes();
	}
}













