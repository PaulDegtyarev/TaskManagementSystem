package TaskManagementSystem.config;

import TaskManagementSystem.entity.AccountEntity;
import TaskManagementSystem.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public class MyAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication
                .getCredentials()
                .toString();

        Optional<AccountEntity> existsAccount = accountRepository.findByEmail(email);

        if (existsAccount.isEmpty()) {
            throw new BadCredentialsException("Неверный email");
        }

        if (!password.equals(
                existsAccount
                        .get()
                        .getPassword()
            )) {
            throw new BadCredentialsException("Неверный пароль");
        }

        return authenticateAgainstThirdPartyAndGetAuthentication(email, password);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    private UsernamePasswordAuthenticationToken authenticateAgainstThirdPartyAndGetAuthentication(String email, String password) {
        String role = accountRepository
                .findByEmail(email)
                .get()
                .getRoleEntity()
                .getRole();

        UserDetails principal = User
                .builder()
                .username(email)
                .password(password)
                .roles(role)
                .build();

        return new UsernamePasswordAuthenticationToken(principal, password, principal.getAuthorities());
    }
}
