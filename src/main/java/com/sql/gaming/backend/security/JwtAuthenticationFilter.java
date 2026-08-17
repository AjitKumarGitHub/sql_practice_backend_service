package com.sql.gaming.backend.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;

	private final CustomUserDetailsService customUserDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		 

		try {

			String authHeader = request.getHeader("Authorization");

			System.out.println("AUTH HEADER: " + authHeader);

			if (authHeader == null || !authHeader.startsWith("Bearer ")) {

				System.out.println("NO BEARER TOKEN");

				filterChain.doFilter(request, response);
				return;
			}

			String token = authHeader.substring(7);

			System.out.println("JWT TOKEN FOUND");

			String userName = jwtService.extractUsername(token);

			System.out.println("USERNAME FROM JWT: " + userName);

			if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {

				UserDetails userDetails = customUserDetailsService.loadUserByUsername(userName);

				System.out.println("USER FROM DB: " + userDetails.getUsername());

				System.out.println("AUTHORITIES: " + userDetails.getAuthorities());

				boolean valid = jwtService.isTokenValid(token, userDetails);

				System.out.println("JWT VALID: " + valid);

				if (valid) {

					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());

					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

					SecurityContextHolder.getContext().setAuthentication(authentication);

					System.out.println("SECURITY CONTEXT AUTHENTICATED");
				}
			}

		} catch (Exception ex) {

			System.out.println("JWT FILTER ERROR: " + ex.getMessage());

			ex.printStackTrace();

			SecurityContextHolder.clearContext();
		}

		filterChain.doFilter(request, response);
	}
}