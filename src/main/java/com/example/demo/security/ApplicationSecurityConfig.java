package com.example.demo.security;

import com.example.demo.student.Student;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import static com.example.demo.security.ApplicationUserPermission.*;
import static com.example.demo.security.ApplicationUserRole.*;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {
  private final PasswordEncoder passwordEncoder;

  public ApplicationSecurityConfig(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
/*
            .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .and()
*/
            .csrf().disable()
            .authorizeRequests()
            .antMatchers("/", "/index", "/css/*", "/js/*")
            .permitAll()
            .antMatchers("/api/**").hasRole(STUDENT.name())
/*
             replaced with annotation @PreAuthorize in Controller
            .antMatchers(HttpMethod.GET, "/management/api/**").hasAnyRole(ADMIN.name(), ADMINTRAINEE.name())
            .antMatchers(HttpMethod.DELETE,"/management/api/**").hasAuthority(COURSE_WRITE.getPermission())
            .antMatchers(HttpMethod.POST,"/management/api/**").hasAuthority(COURSE_WRITE.getPermission())
            .antMatchers(HttpMethod.PUT,"/management/api/**").hasAuthority(COURSE_WRITE.getPermission())
*/
            .anyRequest()
            .authenticated()
            .and()
            .formLogin();
  }

  @Override
  @Bean
  protected UserDetailsService userDetailsService() {
    UserDetails annaSmithUser = User.builder()
            .username("annasmith")
            .password(passwordEncoder.encode("password"))
//            .roles(STUDENT.name()) // ROLE_STUDENT
            .authorities(STUDENT.getGrantedAuthorities())
            .build();

    UserDetails lindaUser = User.builder()
            .username("linda")
            .password(passwordEncoder.encode("password123"))
//            .roles(ADMIN.name()) // ROLE_ADMIN
            .authorities(ADMIN.getGrantedAuthorities())
            .build();

    UserDetails tomUser = User.builder()
            .username("tom")
            .password(passwordEncoder.encode("password123"))
//            .roles(ADMINTRAINEE.name()) // ROLE_ADMINTRAINEE
            .authorities(ADMINTRAINEE.getGrantedAuthorities())
            .build();



    return new InMemoryUserDetailsManager(
            annaSmithUser,
            lindaUser,
            tomUser

    );
  }
}
