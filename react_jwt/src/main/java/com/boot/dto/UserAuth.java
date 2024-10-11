package com.boot.dto;

import lombok.Data;

@Data
public class UserAuth {
	private int auth_no;
	private String userId;
	private String auth;
	
	public UserAuth() {}
	
	public UserAuth(String userId, String auth) {
		this.userId = userId;
		this.auth = auth;
	}
}
