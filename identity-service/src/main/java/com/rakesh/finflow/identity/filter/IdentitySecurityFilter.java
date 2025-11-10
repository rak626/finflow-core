package com.rakesh.finflow.identity.filter;

import com.rakesh.finflow.identity.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class IdentitySecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;
    //    private final UserService userService;
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException {

        String uri = req.getRequestURI();
        String username;

        try {
            if (isWhitelisted(uri)) {
                chain.doFilter(req, res);
                return;
            }

            String jwt = getAuthFromRequest(req);
            if (StringUtils.hasText(jwt) && tokenService.validateToken(jwt)) {

                username = tokenService.getUsernameFromJWT(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("Authorities for {} => {}", username, userDetails.getAuthorities());


                // For audit/logging
//                userService.updateLastLoginTime(username);
                log.debug("Authenticated request from user: {} -> {}", username, uri);
            }

            chain.doFilter(req, res);
        } catch (Exception e) {
            log.error("Authentication error for URI {}: {}", uri, e.getMessage(), e);
            generateErrorResponse(res, HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }
    }

    private boolean isWhitelisted(String uri) {
        return PATH_MATCHER.match("/identity/api/v1/auth/register", uri)
                || PATH_MATCHER.match("/identity/api/v1/auth/login", uri);
    }

    private String getAuthFromRequest(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void generateErrorResponse(HttpServletResponse res, HttpStatus status, String message) throws IOException {
        res.setStatus(status.value());
        res.setContentType("application/json");
        res.getWriter().write("{\"error\": \"" + message + "\"}");
        res.getWriter().flush();
    }
}
