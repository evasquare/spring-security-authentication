package com.evasquare.username_password_auth;

import static org.springframework.security.config.Customizer.withDefaults;
import java.util.function.Supplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain applicationSecurity(HttpSecurity http) throws Exception {
        http
                .sessionManagement((auth) -> auth
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false))
                .sessionManagement((session) -> session
                        .sessionFixation(
                                (sessionFixation) -> sessionFixation
                                        .newSession()))
                .httpBasic(withDefaults())
                .formLogin(AbstractHttpConfigurer::disable)
                // .headers(headers -> headers
                // .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .cors(AbstractHttpConfigurer::disable)
                // .csrf(AbstractHttpConfigurer::disable)
                .securityMatcher("/**")
                .authorizeHttpRequests(registry -> registry
                        // .requestMatchers("/h2-console").permitAll()
                        // .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/auth/logout").permitAll()
                        .requestMatchers("/auth/join").permitAll()
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/auth/get-username")
                        .authenticated()
                        .anyRequest().authenticated())
                .csrf((csrf) -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler()));;
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}


/**
 * Code Source:
 * https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html#csrf-integration-javascript
 */
final class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {
    private final CsrfTokenRequestHandler plain = new CsrfTokenRequestAttributeHandler();
    private final CsrfTokenRequestHandler xor = new XorCsrfTokenRequestAttributeHandler();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            Supplier<CsrfToken> csrfToken) {
        /*
         * Always use XorCsrfTokenRequestAttributeHandler to provide BREACH protection of the
         * CsrfToken when it is rendered in the response body.
         */
        this.xor.handle(request, response, csrfToken);
        /*
         * Render the token value to a cookie by causing the deferred token to be loaded.
         */
        csrfToken.get();
    }

    @Override
    public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
        String headerValue = request.getHeader(csrfToken.getHeaderName());
        /*
         * If the request contains a request header, use CsrfTokenRequestAttributeHandler to resolve
         * the CsrfToken. This applies when a single-page application includes the header value
         * automatically, which was obtained via a cookie containing the raw CsrfToken.
         *
         * In all other cases (e.g. if the request contains a request parameter), use
         * XorCsrfTokenRequestAttributeHandler to resolve the CsrfToken. This applies when a
         * server-side rendered form includes the _csrf request parameter as a hidden input.
         */
        return (StringUtils.hasText(headerValue) ? this.plain : this.xor)
                .resolveCsrfTokenValue(request, csrfToken);
    }
}


// class JSONUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
// private boolean postOnly = true;

// @Override
// public Authentication attemptAuthentication(HttpServletRequest request,
// HttpServletResponse response) throws AuthenticationException {
// if (this.postOnly && !request.getMethod().equals("POST")) {
// throw new AuthenticationServiceException(
// "Authentication method not supported: " + request.getMethod());
// }

// String username = obtainUsername(request);
// username = (username != null) ? username.trim() : "";
// String password = obtainPassword(request);
// password = (password != null) ? password : "";

// UsernamePasswordAuthenticationToken authRequest =
// UsernamePasswordAuthenticationToken.unauthenticated(username, password);

// // Allow subclasses to set the "details" property
// setDetails(request, authRequest);

// return this.getAuthenticationManager().authenticate(authRequest);
// }
// }
