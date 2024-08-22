package TaskManagementSystem.dataStore.impl;

import TaskManagementSystem.dataStore.AuthenticationDS;
import TaskManagementSystem.dto.serviceResponse.RegistrationServiceResponseModel;
import TaskManagementSystem.dto.dbo.RegistrationDBO;
import TaskManagementSystem.entity.AccountEntity;
import TaskManagementSystem.entity.RoleEntity;
import TaskManagementSystem.repository.AccountRepository;
import TaskManagementSystem.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationDSImpl implements AuthenticationDS {
    private RoleRepository roleRepository;
    private AccountRepository accountRepository;

    @Autowired
    public AuthenticationDSImpl(RoleRepository roleRepository, AccountRepository accountRepository) {
        this.roleRepository = roleRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public RegistrationServiceResponseModel registration(RegistrationDBO dto) {
        RoleEntity roleEntity = roleRepository.findByRole(dto.getRole());

        AccountEntity newAccount = new AccountEntity(
                dto.getEmail(),
                dto.getPassword(),
                dto.getFirstName(),
                dto.getLastName(),
                roleEntity.getRoleId()
        );
        newAccount.setRoleEntity(roleEntity);

        accountRepository.save(newAccount);

        return new RegistrationServiceResponseModel(
                newAccount.getAccountId(),
                newAccount.getEmail(),
                newAccount.getFirstname(),
                newAccount.getLastname(),
                newAccount.getRoleEntity().getRole()
        );
    }
}
