package com.ktb.discussionboard.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private Authentication authentication;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(jwtTokenProvider);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Filter authenticates a valid access token")
    void doFilter_withAccessToken_setsAuthentication() throws Exception {
        MockHttpServletRequest request = requestWithBearerToken("access-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        given(jwtTokenProvider.validateAccessToken("access-token")).willReturn(true);
        given(jwtTokenProvider.getAuthentication("access-token"))
                .willReturn(authentication);

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isSameAs(authentication);
    }

    @Test
    @DisplayName("Filter does not authenticate a refresh token")
    void doFilter_withRefreshToken_doesNotSetAuthentication() throws Exception {
        MockHttpServletRequest request = requestWithBearerToken("refresh-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        given(jwtTokenProvider.validateAccessToken("refresh-token")).willReturn(false);

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        then(jwtTokenProvider).should(never()).getAuthentication("refresh-token");
    }

    private MockHttpServletRequest requestWithBearerToken(String token) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(
                JwtAuthenticationFilter.TOKEN_HEADER,
                JwtAuthenticationFilter.TOKEN_PREFIX + token
        );
        return request;
    }
}
