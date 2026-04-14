package com.nikestore.shoeshop.config;

import com.nikestore.shoeshop.service.AppUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(AppUserDetailsService userDetailsService,
                                                            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   DaoAuthenticationProvider provider) throws Exception {

        http.authenticationProvider(provider);

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/",
                        "/shop/**",
                        "/product/**",
                        "/signin",
                        "/login",
                        "/register",
                        "/forgot-password",
                        "/cart/**",
                        "/wishlist/**",
                        "/orders/**",
                        "/checkout/**",
                        "/css/**",
                        "/js/**",
                        "/img/**",
                        "/webjars/**"
                ).permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
        );

        http.formLogin(form -> form
                .loginPage("/signin")              // trang login
                .loginProcessingUrl("/login")      // xử lý login
                .defaultSuccessUrl("/", true)
                .failureUrl("/signin?error=true")
                .permitAll()
        );

        http.logout(logout -> logout
                .logoutSuccessUrl("/?logout=true")
                .permitAll()
        );

        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
        );

        // CSRF enabled for security

        return http.build();
    }

}