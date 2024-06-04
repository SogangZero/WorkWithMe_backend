package com.wwme.wwme.login;

import com.wwme.wwme.login.domain.dto.CustomOAuth2User;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class WwmAuthenticationToken extends AbstractAuthenticationToken {
    private final CustomOAuth2User principal;
    public WwmAuthenticationToken(CustomOAuth2User principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        super.setAuthenticated(true);
        this.principal = principal;
    }

    // Don't need since this represents an authenticated user
    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
