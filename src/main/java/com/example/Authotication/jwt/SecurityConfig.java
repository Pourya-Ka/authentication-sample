package com.example.Authotication.jwt;

import com.example.Authotication.repository.Repository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.Filter;

import static com.example.Authotication.config.UserRole.*;


@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private Repository repository;
    private JwtUtils jwtUtils;
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterAfter(new JwtTokenFilter(jwtUtils,repository), BasicAuthenticationFilter.class)
                .authorizeHttpRequests()
                .antMatchers("/authentication/").permitAll()
                .antMatchers("/authentication/refresh_token_valid").permitAll()
                .antMatchers("/authentication/users").hasAnyAuthority(ADMIN.name(),USER.name())
                .antMatchers("/authentication/users/**").hasAnyAuthority(ADMIN.name())
                .antMatchers("/authentication/add_user").hasRole(ADMIN.name())
                .anyRequest()
                .authenticated();
//                .and()
//                .httpBasic();

    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        final DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
//        return NoOpPasswordEncoder.getInstance();
    }

        @Bean
    public UserDetailsService userDetailsService() {
        return name -> User.builder().username(repository.userFindByName(name).getUsername())
                .password(repository.userFindByName(name).getPassword())
                .roles(repository.userFindByName(name).getRole().name())
                .build();
    }
//    @Override
//    protected UserDetailsService userDetailsService() {
//        return new InMemoryUserDetailsManager(
//                repository.userDetails()
//        );
//    }
}
