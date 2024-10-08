package TaskManagementSystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new MyUserDetailsService();
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .cors()
                .and()

                .authorizeHttpRequests()
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/task").hasRole("AUTHOR")
                .requestMatchers(HttpMethod.PUT, "/task/me/{taskId}", "/task/author/{taskId}/status", "/task/{taskId}/executor/{executorId}").hasRole("AUTHOR")
                .requestMatchers(HttpMethod.GET, "/task/me", "/task/me/{taskId}").hasRole("AUTHOR")
                .requestMatchers(HttpMethod.DELETE, "/task/me", "/task/me/{taskId}").hasRole("AUTHOR")
                .requestMatchers(HttpMethod.PUT, "/task/executor/{taskId}/status").hasRole("EXECUTOR")
                .requestMatchers("/task/**").authenticated()
                .anyRequest()
                .permitAll()

                .and()
                .formLogin()
                .loginPage("/auth")
                .loginProcessingUrl("/auth/signin")
                .defaultSuccessUrl("/auth/success")
                .failureUrl("/auth/invalid")
                .permitAll()

                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/auth/signin")
                .permitAll()

                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new MyAuthenticationEntryPoint());

        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public InternalResourceViewResolver jspViewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/templates/");
        resolver.setSuffix(".html");
        return resolver;
    }
}
