package io.github.stanislav.smartparkingsystem.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request,
	                                HttpServletResponse response,
	                                FilterChain filterChain) throws ServletException, IOException {

		String token = jwtUtil.resolveToken(request);

		if(token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			try {
				Claims claims = jwtUtil.validateToken(token);
				Authentication authentication = jwtUtil.buildAuthentication(claims);

				if(authentication != null) {
					SecurityContextHolder.getContext().setAuthentication(authentication);
				} else {
					SecurityContextHolder.clearContext();
				}
			} catch(JwtException | IllegalArgumentException ex) {
				SecurityContextHolder.clearContext();

			}
		}

		filterChain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		return !request.getServletPath().startsWith("/api/admin");
	}
}