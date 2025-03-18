package KalendarUspomena.Security;
import KalendarUspomena.Util.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private List<String>  PUBLIC_PATHS;
  private static final AntPathMatcher antPathMatcher = new AntPathMatcher();
  private final JwtUtil jwtUtil;
  private final UserDetailsService userDetailsService;

  public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService, SecurityProperties securityProperties) {
    this.jwtUtil = jwtUtil;
    this.userDetailsService = userDetailsService;
    this.PUBLIC_PATHS = securityProperties.getPublicPaths().stream().toList();
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    // Izuzmite javne rute
    for (int i = 0; i < PUBLIC_PATHS.size(); i++) {
      if (antPathMatcher.match(PUBLIC_PATHS.get(i),request.getRequestURI())) {
        chain.doFilter(request, response);
        return;
      }
    }

    try {
      final String authorizationHeader = request.getHeader("Authorization");

      String username = null;
      String jwt = null;

      if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
        jwt = authorizationHeader.substring(7);
        username = jwtUtil.extractUsername(jwt);
        jwtUtil.CheckIsAccesToken(jwt);
      } else throw new JwtException("Bearer token is not provided.");

      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
        if (jwtUtil.isTokenValid(jwt, userDetails.getUsername())) {
          var authToken = new UsernamePasswordAuthenticationToken(
              userDetails, null, userDetails.getAuthorities());
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authToken);
        } else throw new JwtException("Token is not valid.");
      } else throw new JwtException("Token is not valid.");

      chain.doFilter(request, response);
    }catch (JwtException e) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType("application/json");
      response.getWriter().write("JWT authentication failed : "+e.getMessage());
    } catch (Exception e) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      response.setContentType("application/json");
      response.getWriter().write("{\"error\": \"Unexpected server error occurred\"}");
    }
  }
}
