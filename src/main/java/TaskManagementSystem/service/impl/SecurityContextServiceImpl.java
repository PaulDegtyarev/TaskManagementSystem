package TaskManagementSystem.service.impl;

import TaskManagementSystem.service.SecurityContextService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityContextServiceImpl implements SecurityContextService {
    @Override
    public boolean isAnonymousUser() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal() == "anonymousUser";
    }
}