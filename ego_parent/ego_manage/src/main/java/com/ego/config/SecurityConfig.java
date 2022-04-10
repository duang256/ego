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
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static void main(String[] args) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        //BCryptPasswordEncoder的原理是Hash多个密文，数据库中存放的是密文之一，match的时候hash密码，看多个密文中是否包含数据库中的

        //$2a$10$F.840hSy3OfOfR5hmvGbvOEGmZIEO7Qj0q4GRaNJx0y2.pGHEinJa
        //$2a$10$OptjkSjFHK2cedPw3Tr/HesYrWgOrF5uQFN0xCq4RNjzyyktfe1KW
        //$2a$10$nWI/Xm0wQM/GwWXwrMyfUeEZrev/crAYsTnM3Ni8f07FQrOexw4HC
        //$2a$10$3jplR95VRq0BFp91nRxFZe1DoX/2fnXtRGlFoHwuAF2vYvfhvQzWS
        //$2a$10$7jlENPrBI2N2lzRac6xi2uctfZqlE4oxoKjuiVkDuFH/NvgS3jmA6
//        for(int i = 0;i < 5;i++){
//            System.out.println( encoder.encode("admin"));
//
//        }
        System.out.println(encoder.matches("admin", "$2a$10$F.840hSy3OfOfR5hmvGbvOEGmZIEO7Qj0q4GRaNJx0y2.pGHEinJa"));
        System.out.println(encoder.matches("admin", "$2a$10$OptjkSjFHK2cedPw3Tr/HesYrWgOrF5uQFN0xCq4RNjzyyktfe1KW"));
        System.out.println(encoder.matches("admin", "$2a$10$nWI/Xm0wQM/GwWXwrMyfUeEZrev/crAYsTnM3Ni8f07FQrOexw4HC"));
        System.out.println(encoder.matches("admin", "$2a$10$3jplR95VRq0BFp91nRxFZe1DoX/2fnXtRGlFoHwuAF2vYvfhvQzWS"));
        System.out.println(encoder.matches("admin", "$2a$10$7jlENPrBI2N2lzRac6xi2uctfZqlE4oxoKjuiVkDuFH/NvgS3jmA6"));
        System.out.println("-----------------------------");
        System.out.println(encoder.matches("admin", "$2a$10$l/vgowvefJk.vUmj3I4zyuhBDFTyQAa.bDe86wFjkpXMoYRWo3mNq"));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                .loginProcessingUrl("/login")
                .successForwardUrl("/loginSuccess")
                .failureForwardUrl("/loginFail")
                .loginPage("/");
        http.authorizeRequests()
                //放行所有请求和静态资源
                .antMatchers("/", "/loginFail", "/css/**", "/js/**").permitAll()
                .anyRequest().authenticated();
        //关闭csrf
        http.csrf().disable();
        //解决iframe中图片提交bug，这是由于spring security引起的
        http.headers().frameOptions().disable();
    }
}
