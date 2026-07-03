package com.folioframe.global.auth;

import com.folioframe.global.auth.exception.AuthException;
import com.folioframe.global.auth.exception.code.AuthErrorCode;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CurrentMemberIdArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentMemberId.class)
                && Long.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                   NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long memberId = null;
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            memberId = userDetails.member().getId();
        }

        boolean required = parameter.getParameterAnnotation(CurrentMemberId.class).required();
        if (memberId == null && required) {
            throw new AuthException(AuthErrorCode.EMPTY_AUTHENTICATION);
        }
        return memberId;
    }
}
