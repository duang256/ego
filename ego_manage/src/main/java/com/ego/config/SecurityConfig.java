package com.ego.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Bean
    protected PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                .loginProcessingUrl("/login")
                .successForwardUrl("/loginSuccess")
                .loginPage("/");
        http.authorizeRequests()
                //放行所有请求和静态资源
                .antMatchers("/","/css/**","/js/**").permitAll()
                .anyRequest().authenticated();
        //关闭csrf
        http.csrf().disable();
        //解决iframe中图片提交bug，这是由于spring security引起的
        http.headers().frameOptions().disable();
    }
}
