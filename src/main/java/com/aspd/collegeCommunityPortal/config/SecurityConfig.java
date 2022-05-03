package com.aspd.collegeCommunityPortal.config;

import com.aspd.collegeCommunityPortal.filter.JwtAccessDeniedHandler;
import com.aspd.collegeCommunityPortal.filter.JwtAuthenticationEntryPoint;
import com.aspd.collegeCommunityPortal.filter.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtAccessDeniedHandler jwtAccessDeniedHandler;
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(getPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.exceptionHandling().accessDeniedHandler(jwtAccessDeniedHandler).authenticationEntryPoint(jwtAuthenticationEntryPoint);
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        http.csrf().disable().cors();
        http.authorizeRequests().antMatchers("/auth/login").permitAll().antMatchers("/auth/signup").permitAll().antMatchers("/auth/forgot/password").permitAll().antMatchers("/auth/activate/account/**").permitAll().antMatchers("/api/post/local/storage/download/**").permitAll().antMatchers("/auth/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN").antMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN").antMatchers("/api/post/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN");
        http.authorizeRequests().anyRequest().authenticated();

    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
