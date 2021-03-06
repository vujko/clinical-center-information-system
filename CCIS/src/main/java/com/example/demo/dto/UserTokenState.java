package com.example.demo.dto;

import java.util.List;

public class UserTokenState {
	
    private String accessToken;
    private Long expiresIn;

    private List<String> authorities;
    private String email;
    private boolean isPasswordChanged;

    public UserTokenState() {
        this.accessToken = null;
        this.expiresIn = null;
    }

    public UserTokenState(String accessToken, long expiresIn, List<String> authorities, String email, boolean isPasswordChanged) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.authorities = authorities;
        this.email = email;
        this.isPasswordChanged = isPasswordChanged;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isPasswordChanged() {
        return isPasswordChanged;
    }

    public void setPasswordChanged(boolean passwordChanged) {
        isPasswordChanged = passwordChanged;
    }
}