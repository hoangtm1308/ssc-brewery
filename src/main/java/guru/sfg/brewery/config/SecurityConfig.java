package guru.sfg.brewery.config;

import guru.sfg.brewery.security.CustomPasswordEncoderFactories;
import guru.sfg.brewery.security.RestHeaderAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    //Method dùng để setup và cấu trúc filter những rest header
    public RestHeaderAuthFilter restHeaderAuthFilter(AuthenticationManager authenticationManager){
        RestHeaderAuthFilter filter = new RestHeaderAuthFilter(new AntPathRequestMatcher("/api/**"));
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    //Override các config trong WebSecurityConfigurerAdapter
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //Config Spring Security để sử dụng filter ở trên
        http.addFilterBefore(restHeaderAuthFilter(authenticationManager()),
                UsernamePasswordAuthenticationFilter.class)
                .csrf().disable();

        // "/*" cho phép các tầng truy cập cùng level với dấu sao
        // "/**" cho phép toàn bộ sau dấu /
        http
                .authorizeRequests(authorize -> {
                    authorize
                            .antMatchers("/", "/webjars/**", "/login", "/resources/**").permitAll()
//                        .antMatchers("/beers/find","/beer*").permitAll() //config thêm find beer không cần đăng nhập
                            .antMatchers(HttpMethod.GET, "/api/v1/beer/**").permitAll()
                            .mvcMatchers(HttpMethod.GET, "/api/v1/beerUpc/{upc}").permitAll();

                })
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin().and()
                .httpBasic();
    }

    //Create 2 User Details and put into User In-Memory Details Manager
//    @Override
//    @Bean
//    protected UserDetailsService userDetailsService() {
//        UserDetails admin = User.withDefaultPasswordEncoder()
//                .username("hoangtm")
//                .password("1308n5ggp")
//                .roles("ADMIN")
//                .build();
//
//        UserDetails user = User.withDefaultPasswordEncoder()
//                .username("user")
//                .password("password")
//                .roles("USER")
//                .build();
//
//        return new InMemoryUserDetailsManager(admin, user);
//    }

    @Bean
        //Config NoOpPasswordEncoder
    PasswordEncoder passwordEncoder() {
        //Spring Security giữ để support những hệ thống cổ
        //return NoOpPasswordEncoder.getInstance();
        //return new LdapShaPasswordEncoder();
        //return new StandardPasswordEncoder();
        //return new BCryptPasswordEncoder();
//        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        return CustomPasswordEncoderFactories.customDelegatingPasswordEncoder();
    }

    //Create User doing Authentication Fluent API (Authentication Manager Builder) - Difference way
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("hoangtm")
//                .password("{noop}1308n5ggp") // dùng {noop} cho những password cần encode
                .password("{ldap}{SSHA}BBhtzKbfFCDcTFuUotq3Qd0jxLulMqzNEDaTaw==")
                .roles("ADMIN")
                .and()
                .withUser("user")
//                .password("{noop}password")  // dùng {noop} cho những password cần encode
                .password("{bcrypt}$2a$10$sUAPRNUFBCK7C4pbx2aNbOMWrHGTD6tn61abtE633yJ9sDe.rviem")
                .roles("user")
                .and()
                .withUser("scott")
//                .password("{noop}tiger")
                .password("{bcrypt15}$2a$15$68GpwChurZgcS3.rd8FVU.95n2/NTXGJz8PZ1Z08O64cCSt0tygHK")
                .roles("CUSTOMER");
    }
}