package com.wwme.wwme.login.aop;

import com.wwme.wwme.login.service.JWTUtilService;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {
    private final UserRepository userRepository;
    private final JWTUtilService jwtUtilService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Login.class)
                && User.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory)
            throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String accessToken = request.getHeader("access");

        //For Supervisor
        if (request.getHeader("developer") != null) {
            return userRepository.findByUserKey("developmentUserKey").get();
        }
        /*
        *  User를 가지고 올 때, 토큰을 재사용하는 것이 아니라 contextHolder에서 꺼내오는 방법
        * */


        String userKey = jwtUtilService.getUserKey(accessToken);

        User emptyUser = new User();
        emptyUser.setId(-1L);

        User user = userRepository.findByUserKey(userKey).orElse(emptyUser);

        return user;
    }
}
