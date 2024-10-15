package kg.angryelizar.resourcebooking.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final static String ADMIN_ROLE = "ADMIN";
    private final static String USER_ROLE = "USER";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(Customizer.withDefaults())
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        // Регистрация доступна только пользователям, которые не вошли в систему
                        .requestMatchers(HttpMethod.POST, "/registration").anonymous()
                        // Перечисление эндпоинтов, которые доступны для просмотра всем
                        .requestMatchers(HttpMethod.GET, "/resources").permitAll()
                        .requestMatchers(HttpMethod.GET, "/resources/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/payments/methods").permitAll()
                        // Те пути, которые доступны только обычным пользователям (роль USER)
                        .requestMatchers(HttpMethod.POST, "/bookings/**").hasAuthority(USER_ROLE)
                        .requestMatchers(HttpMethod.DELETE, "/bookings/**").hasAuthority(USER_ROLE)
                        .requestMatchers(HttpMethod.POST, "/bookings/**/payments").hasAuthority(USER_ROLE)
                        .requestMatchers(HttpMethod.GET, "/profile/bookings").hasAuthority(USER_ROLE)
                        .requestMatchers(HttpMethod.GET, "/profile/payments").hasAuthority(USER_ROLE)
                        // Те пути, которые доступны только администраторам (роль ADMIN)
                        .requestMatchers(HttpMethod.GET, "/payments/").hasAuthority(ADMIN_ROLE)
                        .requestMatchers(HttpMethod.GET, "/bookings/").hasAuthority(ADMIN_ROLE)
                        .requestMatchers(HttpMethod.POST, "/resources").hasAuthority(ADMIN_ROLE)
                        .requestMatchers(HttpMethod.PUT, "/resources/**").hasAuthority(ADMIN_ROLE)
                        .requestMatchers(HttpMethod.DELETE, "/resources/**").hasAuthority(ADMIN_ROLE)
                        .requestMatchers(HttpMethod.PUT, "/payments/**").hasAuthority(ADMIN_ROLE)
                        .requestMatchers(HttpMethod.DELETE, "/payments/**").hasAuthority(ADMIN_ROLE)
                        .requestMatchers(HttpMethod.POST, "/bookings/**").hasAuthority(ADMIN_ROLE)
                        .requestMatchers(HttpMethod.DELETE, "/bookings/**").hasAuthority(ADMIN_ROLE)
                        .anyRequest().permitAll());
        return http.build();
    }
}
