package TaskManagementSystem.config;

import TaskManagementSystem.entity.AccountEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;

public class MyUserDetails implements UserDetails {
    private AccountEntity accountEntity;

    public MyUserDetails(AccountEntity accountEntity) {
        this.accountEntity = accountEntity;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays
                .stream(accountEntity
                        .getRoleEntity()
                        .getRole()
                        .split(", "))
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @Override
    public String getPassword() {
        return accountEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return accountEntity.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Integer getId() {
        return accountEntity.getAccountId();
    }

    public String getRole() {
        return accountEntity
                .getRoleEntity()
                .getRole();
    }
}
