package TaskManagementSystem.service.impl;

import TaskManagementSystem.dto.dbo.RegistrationDBO;
import TaskManagementSystem.service.HashService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class HashServiceImpl implements HashService {
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public RegistrationDBO hashPassword(RegistrationDBO dto) {
        String notHashedPassword = dto.getPassword();

        String hashedPassword = encoder.encode(notHashedPassword);

        dto.setPassword(hashedPassword);

        return dto;
    }
}
