package com.example.demo.config;

import com.example.demo.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // ✅ Cú pháp mới
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/register", "/api/users/login").permitAll()  // Cho phép tất cả người dùng truy cập
                        .requestMatchers("/api/users").hasRole("ADMIN")  // Chỉ admin mới có thể xem tất cả người dùng
                        .requestMatchers("/api/users/*").hasAnyRole("USER", "ADMIN")  // User có thể xem thông tin của mình
                        .requestMatchers("/api/users/{id}/block").hasRole("ADMIN")  // Chỉ admin mới có thể block/unblock người dùng
                        // Public APIs: Cho phép tất cả người dùng truy cập
                        .requestMatchers("/api/products", "/api/products/uploads/**", "/api/products/categories", "/api/products/brands").permitAll()
                        .requestMatchers("/api/products/{id}", "/api/products/categories/{id}", "/api/products/brands/{id}").permitAll()

                        // API cho Admin: chỉ admin mới có quyền truy cập
                        .requestMatchers("/api/products/add").hasRole("ADMIN")  // Thêm sản phẩm
                        .requestMatchers("/api/products/{id}").hasRole("ADMIN")  // Cập nhật, xóa sản phẩm
                        .requestMatchers("/api/products/{id}/toggle-block").hasRole("ADMIN")  // Chỉ admin mới có thể block/unblock sản phẩm

                        // API cho User và Admin: cho phép người dùng đăng nhập và xem thông tin sản phẩm
                        .requestMatchers("/api/products").hasAnyRole("USER", "ADMIN") // Xem tất cả sản phẩm
                        .requestMatchers("/api/products/{id}").hasAnyRole("USER", "ADMIN") // Xem thông tin sản phẩm
                        // Các API cho User
                        .requestMatchers("/api/orders/user/{userId}").hasAnyRole("USER", "ADMIN")  // Xem đơn hàng của user
                        .requestMatchers("/api/orders").hasRole("USER")  // Tạo đơn hàng

                        // Các API cho Admin
                        .requestMatchers("/api/orders").hasRole("ADMIN")  // Xem tất cả đơn hàng
                        .requestMatchers("/api/orders/{id}").hasRole("ADMIN")  // Cập nhật, xóa đơn hàng
                        // Các API cho User
                        .requestMatchers("/api/brands").permitAll()  // Tất cả người dùng có thể xem danh sách thương hiệu

                        // Các API cho Admin
                        .requestMatchers("/api/brands").hasRole("ADMIN")  // Chỉ admin có quyền tạo thương hiệu
                        .requestMatchers("/api/brands/{id}").hasRole("ADMIN")  // Chỉ admin có quyền cập nhật, xóa thương hiệu

                        .anyRequest().authenticated()  // Bảo vệ các yêu cầu còn lại
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ✅ Cấu hình AuthenticationManager theo cách mới
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
        return authBuilder.build();
    }
}
