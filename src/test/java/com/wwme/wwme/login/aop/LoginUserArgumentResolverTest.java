package com.wwme.wwme.login.aop;

import com.wwme.wwme.login.jwt.JWTUtil;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class LoginUserArgumentResolverTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private JWTUtil jwtUtil;

    @InjectMocks
    private LoginUserArgumentResolver resolver;

    @Mock
    private HttpServletRequest request;

    @Mock
    private NativeWebRequest webRequest;

    @Mock
    private MethodParameter methodParameter;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("supportsParameter Test")
    public void supportsParameterTest() throws NoSuchMethodException {
        Method method = TestController.class.getMethod("testMethod", User.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);

        boolean supports = resolver.supportsParameter(methodParameter);
        assertThat(supports).isTrue();
    }

    @Test
    @DisplayName("User annotation 작동 확인")
    public void resolveArgumentValidTest() throws Exception {
        String accessToken = "validToken";
        String userKey = "userKey";
        User user = new User();
        user.setUserKey(userKey);

        when(webRequest.getNativeRequest())
                .thenReturn(request);
        when(request.getHeader("access"))
                .thenReturn(accessToken);
        when(jwtUtil.getUserKey(accessToken))
                .thenReturn(userKey);
        when(userRepository.findByUserKey(userKey))
                .thenReturn(Optional.of(user));

        Method method = TestController.class.getMethod("testMethod", User.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);

        Object resolvedArgument = resolver.resolveArgument(methodParameter,
                new ModelAndViewContainer(),
                webRequest,
                null);

        assertThat(user).isEqualTo(resolvedArgument);
    }

    @Test
    @DisplayName("token이 없는 경우에 user는 null로 반환")
    public void resolveArgumentMissingTokenTest() throws Exception {
        when(webRequest.getNativeRequest())
                .thenReturn(request);
        when(request.getHeader("access"))
                .thenReturn(null);

        Method method = TestController.class.getMethod("testMethod", User.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);

        Object resolvedArgument = resolver.resolveArgument(methodParameter,
                new ModelAndViewContainer(),
                webRequest,
                null);
        assertThat(resolvedArgument).isInstanceOf(User.class);
        User user = (User) resolvedArgument;
        assertThat(user.getId()).isEqualTo(-1L);
    }

    @Test
    @DisplayName("userRepository에 없는 경우 null 반환")
    public void resolveArgumentUserNotFoundTest() throws Exception {
        String accessToken = "validToken";
        String userKey = "userKey";

        when(webRequest.getNativeRequest())
                .thenReturn(request);
        when(request.getHeader("access"))
                .thenReturn(accessToken);
        when(jwtUtil.getUserKey(accessToken))
                .thenReturn(userKey);
        when(userRepository.findByUserKey(userKey))
                .thenReturn(Optional.empty());

        Method method = TestController.class.getMethod("testMethod", User.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);

        Object resolvedArgument = resolver.resolveArgument(methodParameter,
                new ModelAndViewContainer(),
                webRequest,
                null);

        assertThat(resolvedArgument).isInstanceOf(User.class);
        User user = (User) resolvedArgument;
        assertThat(user.getId()).isEqualTo(-1L);
    }

    static class TestController {
        public void testMethod(@Login User user) {
            System.out.println("user = " + user);
        }
    }
}
