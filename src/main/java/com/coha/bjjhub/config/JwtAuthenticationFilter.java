package com.coha.bjjhub.config;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.coha.bjjhub.utils.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;


    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 요청에서 Authorization 헤더를 추출합니다.
            final String header = request.getHeader("Authorization");

            String userId = null;
            String jwt = null;

            // 헤더가 null이 아니고 "Bearer "로 시작하는 경우 JWT 토큰을 추출합니다.
            if (header != null && header.startsWith("Bearer ")) {
                jwt = header.substring(7);
                userId = jwtUtil.getUserIdFromToken(jwt);
            }

            // userId가 발견되었고 보안 컨텍스트에 현재 인증이 없는 경우,
            // 새로운 UsernamePasswordAuthenticationToken을 설정합니다.
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userId);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

            // 체인 내 다음 필터로 계속 진행합니다.
            filterChain.doFilter(request, response);
        } catch (SignatureVerificationException sve) {
            throw new JWTVerificationException("Invalid JWT signature", sve);
        } catch (TokenExpiredException tee) {
            throw new JWTVerificationException("JWT token has expired", tee);
        } catch (JWTVerificationException jve) {
            throw new JWTVerificationException("JWT token is invalid", jve);
        }
    }
}

//JWT 유효성 검증 중 발생할 수 있는 예외들을 캐치하면서 해당 예외들을 처리하기 위해 try-catch 블록을 추가하였습니다.
//각 예외 처리 블록 뒤에 return 구문을 추가하여 해당 예외 발생 시, 응답을 반환하고 메서드 실행을 종료하도록 했습니다.