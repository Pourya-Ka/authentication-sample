package com.example.Authotication.jwt;

import com.example.Authotication.repository.Repository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import static com.example.Authotication.config.UserRole.*;


@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private Repository repository;
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests()
                .antMatchers("/authentication/").permitAll()
                .antMatchers("/authentication/refresh_token_valid").permitAll()
                .antMatchers(HttpMethod.GET, "/authentication/users").hasAnyRole(ADMIN.name(),USER.name())
                .antMatchers(HttpMethod.GET, "/authentication/users/**").hasRole(ADMIN.name())
                .antMatchers(HttpMethod.POST, "/authentication/add_user").hasRole(ADMIN.name())
                .anyRequest()
                .authenticated()
                .and()
                .addFilterBefore(new JwtTokenVerifier(), null)
                .httpBasic();

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
