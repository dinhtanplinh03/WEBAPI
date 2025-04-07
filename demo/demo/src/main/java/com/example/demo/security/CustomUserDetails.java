package com.example.demo.security;

import com.example.demo.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    // Trả về quyền của người dùng (ROLE_ADMIN hoặc ROLE_USER)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = "ROLE_" + user.getRole().name(); // vì Spring Security cần prefix "ROLE_"
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // Nếu bạn muốn khóa tài khoản theo cột `blocked`, xử lý tại đây
    @Override
    public boolean isAccountNonLocked() {
        return user.getBlocked() == null || user.getBlocked() == 0;
    }

    @Override
    public boolean isEnabled() {
        return true; // Bạn có thể thêm cột `enabled` nếu muốn kiểm soát rõ hơn
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Nếu không dùng tính năng hết hạn tài khoản, cứ trả true
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Nếu không dùng tính năng hết hạn mật khẩu, cứ trả true
    }

    // Dùng khi bạn cần truy cập dữ liệu gốc
    public User getUser() {
        return user;
    }
}
