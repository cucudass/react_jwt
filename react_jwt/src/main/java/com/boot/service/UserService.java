package com.boot.service;

import com.boot.dto.Users;

import jakarta.servlet.http.HttpServletRequest;

public interface UserService {
	//회원 등록
	public int insert(Users users);
	//회원 조회
	public Users select(int userNo);
	//로그인
	public void login(Users user, HttpServletRequest request);
	//회원 수정
	public int update(Users users);
	//회원 삭제
	public int delete(String user_id);
}
