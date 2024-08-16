package TaskManagementSystem.config;

import TaskManagementSystem.entity.AccountEntity;
import TaskManagementSystem.presenter.AuthenticationPresenter;
import TaskManagementSystem.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AuthenticationPresenter authenticationPresenter;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<AccountEntity> accountEntity = accountRepository.findByEmail(email);

        if (accountEntity.isEmpty()) {
            throw authenticationPresenter.prepareNotFoundView("Аккаунт не найден");
        }

        return accountEntity
                .map(MyUserDetails::new)
                .get();
    }
}
