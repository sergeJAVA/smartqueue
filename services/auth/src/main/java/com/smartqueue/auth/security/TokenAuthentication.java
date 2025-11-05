package com.smartqueue.auth.security;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Getter
public class TokenAuthentication extends UsernamePasswordAuthenticationToken {

    private final TokenData tokenData;

    public TokenAuthentication(TokenData tokenData) {
        super(tokenData, null, tokenData.getAuthorities());
        this.tokenData = tokenData;
    }

}
