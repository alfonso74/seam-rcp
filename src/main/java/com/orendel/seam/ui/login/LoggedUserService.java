package com.orendel.seam.ui.login;

import com.orendel.seam.domain.delivery.User;

public enum LoggedUserService {
	
	INSTANCE;
	
	private User user = null;
	
	
	private LoggedUserService() {
	}

	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
}
