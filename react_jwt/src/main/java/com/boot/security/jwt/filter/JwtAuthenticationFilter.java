package com.boot.security.jwt.filter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.boot.dto.CustomUser;
import com.boot.security.constants.SecurityConstants;
import com.boot.security.jwt.provider.JwtTokenProvider;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

//인증 시도 메소드
//인증 성공 메소드
@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;
	
	public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
		this.authenticationManager = authenticationManager;
		this.jwtTokenProvider = jwtTokenProvider;
		// /login: 필터 url 경로 설정
		setFilterProcessesUrl(SecurityConstants.AUTH_LOGIN_URL);
	}
	
	//인증 시도 메소드
	// /login 경로로 아이디, 비밀번호를 요청하면 이 필터에서 걸려 인증을 시도
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse responese) throws AuthenticationException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		log.info("@# username => " + username);
		log.info("@# password => " + password);
		//사용자 인증정보 객체 생성 -> 아이디, 비밀번호
		Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
		
		//사용자 인증(로그인)
		//CustomUserDetailsService를 통해서 인증
		authentication = authenticationManager.authenticate(authentication);
		log.info("@# authenticationManager => " + authenticationManager);
		log.info("@# authentication => " + authentication);
		log.info("@# 인증 여부 => " + authentication.isAuthenticated());
		
		//인증 실패(아이디, 비밀번호 불일지
		if(!authentication.isAuthenticated()) {
			log.info("인증 실패: 아이디 비밀번호가 일치하지 않습니다.");
			responese.setStatus(401);
		}
		return authentication;
	}
	
	//인증 성공 메소드
	// attemptAuthentication 메소드 호출된 후, attemptAuthentication 객체가 인증되면 호출됨
	// 로그인 인증 성공 --> JWT 토큰 생성
	// 응답(response) 헤더에 JWT 토큰을 담아서 응답
	// Authorization : Bearer + JWT
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
		log.info("인증 성공(auth succcess)");
		
		CustomUser user = (CustomUser) authResult.getPrincipal();
		int userNo = user.getUser().getNo();
		String userId = user.getUser().getUserId();
		List<String> roles = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
		
		//파라미터: 사용자번호, 아이디, 권한
		String token = jwtTokenProvider.createToken(userNo, userId, roles);
		
		response.addHeader(SecurityConstants.TOKEN_HEADER, SecurityConstants.TOKEN_PREFIX + token);
		response.setStatus(200);
	}
}