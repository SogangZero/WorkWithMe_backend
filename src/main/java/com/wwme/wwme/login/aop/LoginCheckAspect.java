package com.wwme.wwme.login.aop;

import com.wwme.wwme.login.jwt.JWTUtil;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.exception.NoSuchUserException;
import com.wwme.wwme.user.exception.NotAuthorizedUserException;
import com.wwme.wwme.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
public class LoginCheckAspect {
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private static final String USER_PARAM = "arg0";

    @Around("@annotation(loginCheck)")
    public Object loginCheck(ProceedingJoinPoint proceedingJoinPoint,
                           LoginCheck loginCheck) throws Throwable {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();

        String authorization = request.getHeader("access");
        if (authorization == null || authorization.isEmpty()) {
            throw new NotAuthorizedUserException("Access Token is null");
        }

        String userKey = jwtUtil.getUserKey(authorization);
        if (userKey == null || userKey.isEmpty()) {
            throw new NotAuthorizedUserException("Access Token is Invalid");
        }

        User user = userRepository.findByUserKey(userKey)
                .orElseThrow(() -> new NoSuchUserException("Not Found User"));

        if (!loginCheck.checkLevel().equals(user.getRole())) {
            throw new NotAuthorizedUserException("Too Low Role for Access");
        }

        Object[] modifiedArgsWithUser = modifyArgsWithUser(user, proceedingJoinPoint);

        return proceedingJoinPoint.proceed(modifiedArgsWithUser);
    }

    private Object[] modifyArgsWithUser(User user, ProceedingJoinPoint proceedingJoinPoint) {
        Object[] parameters = proceedingJoinPoint.getArgs();

        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = signature.getMethod();

        for (int i = 0; i < method.getParameters().length; i++) {
            String parameterName = method.getParameters()[i].getName();
            if (parameterName.equals(USER_PARAM)) {
                parameters[i] = user;
            }
        }

        return parameters;
    }
}
