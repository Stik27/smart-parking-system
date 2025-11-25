package io.github.stanislav.smartparkingsystem.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import io.github.stanislav.smartparkingsystem.config.SecureProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtUtil {

	private final SecureProperties secureProperties;

	private Key getSigningKey() {
		return Keys.hmacShaKeyFor(secureProperties.getJwtSecret().getBytes(StandardCharsets.UTF_8));
	}

	public Claims validateToken(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(getSigningKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

	public String resolveToken(HttpServletRequest request) {
		String bearer = request.getHeader("Authorization");
		if(bearer != null && bearer.startsWith("Bearer ")) {
			return bearer.substring(7);
		}
		return null;
	}

	public Authentication buildAuthentication(Claims claims) {
		String username = claims.getSubject();
		String role = claims.get("role", String.class);

		if(username == null || role == null) {
			return null;
		}

		List<GrantedAuthority> authorities = List.of(
				new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())
		);

		return new UsernamePasswordAuthenticationToken(username, null, authorities);
	}
}