package com.folioframe.global.auth;

import com.folioframe.domain.member.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public record CustomUserDetails(Member member, Map<String, Object> attributes)
        implements UserDetails, OAuth2User {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + member.getMemberType().name()));
    }

    public CustomUserDetails(Member member) {
        this(member, java.util.Collections.emptyMap());
    }

    @Override public Map<String, Object> getAttributes() { return attributes; }
    @Override public String getName() { return member.getLoginId(); }
    @Override public String getPassword() { return member.getPassword(); }
    @Override public String getUsername() { return member.getLoginId(); }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}