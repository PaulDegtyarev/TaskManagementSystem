package TaskManagementSystem.service;

import TaskManagementSystem.dto.dbo.RegistrationDBO;

public interface HashService {
    RegistrationDBO hashPassword(RegistrationDBO dto);
}
