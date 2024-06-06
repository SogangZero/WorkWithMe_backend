package com.wwme.wwme.login.aop;

import com.wwme.wwme.login.service.JWTUtilService;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class LoginCheckAspectTest {
    @Mock
    private JWTUtilService jwtUtilService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @InjectMocks
    private LoginCheckAspect loginCheckAspect;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        ServletRequestAttributes requestAttributes = mock(ServletRequestAttributes.class);
        RequestContextHolder.setRequestAttributes(requestAttributes);
        when(requestAttributes.getRequest())
                .thenReturn(request);
    }

    @Test
    @DisplayName("")
    public void loginCheckWithValidUser() throws Throwable {
        //given
        User user = new User();
        user.setRole("ROLE_USER");

        MethodSignature signature = mock(MethodSignature.class);
        Method method = LoginCheckAspectTest.class.getDeclaredMethod("testMethod", User.class);

        //when
        when(request.getHeader("access"))
                .thenReturn("testToken");
        when(jwtUtilService.getUserKey("testToken"))
                .thenReturn("testKey");
        when(userRepository.findByUserKey("testKey"))
                .thenReturn(Optional.of(user));
        when(signature.getMethod())
                .thenReturn(method);
        when(proceedingJoinPoint.getSignature())
                .thenReturn(signature);
        when(proceedingJoinPoint.getArgs())
                .thenReturn(new Object[]{null});

        for (Object arg : proceedingJoinPoint.getArgs()) {
            System.out.println("arg = " + arg);
        }
        //then
        Object result = loginCheckAspect.loginCheck(proceedingJoinPoint, method.getAnnotation(LoginCheck.class));

        for (Object arg : proceedingJoinPoint.getArgs()) {
            System.out.println("arg = " + arg);
        }

        System.out.println("result = " + result);
        verify(proceedingJoinPoint).proceed(any());
    }

    @LoginCheck(checkLevel = "ROLE_USER")
    private void testMethod(User user) {
        System.out.println(user);
        return;
    }

}
