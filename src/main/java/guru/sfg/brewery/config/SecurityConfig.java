package guru.sfg.brewery.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    //Override các config trong WebSecurityConfigurerAdapter
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // "/*" cho phép các tầng truy cập cùng level với dấu sao
        // "/**" cho phép toàn bộ sau dấu /
        http
            .authorizeRequests(authorize -> {
                authorize
                        .antMatchers("/", "/webjars/**","/login", "/resources/**").permitAll()
                        .antMatchers("/beers/find","/beer*").permitAll() //config thêm find beer không cần đăng nhập
                        .antMatchers(HttpMethod.GET,"/api/v1/beer/**").permitAll()
                        .mvcMatchers(HttpMethod.GET,"/api/v1/beerUpc/{upc}").permitAll();

            })
            .authorizeRequests()
            .anyRequest().authenticated()
            .and()
            .formLogin().and()
            .httpBasic();
    }
}
