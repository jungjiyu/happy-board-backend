package likelion.ideateam3.happy_board.config;

import likelion.ideateam3.happy_board.jwt.CustomAccessDeniedHandler;
import likelion.ideateam3.happy_board.jwt.CustomAuthenticationEntryPoint;
import likelion.ideateam3.happy_board.jwt.JwtAuthenticationFilter;
import likelion.ideateam3.happy_board.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, TokenProvider tokenProvider, AuthenticationManager authenticationManager) throws Exception {
        http
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers(HttpMethod.POST, "/api/members/sign-up").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/members/login").permitAll()
                        // TODO. 요청별 권한 맞게 추가
                        .anyRequest().hasRole("ADMIN"))
                .addFilterBefore(new JwtAuthenticationFilter(tokenProvider, authenticationManager), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler))
        ;

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(TokenProvider tokenProvider) {
        return new ProviderManager(tokenProvider);
    }

}
