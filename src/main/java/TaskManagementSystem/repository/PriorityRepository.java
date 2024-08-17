package TaskManagementSystem.repository;

import TaskManagementSystem.entity.PriorityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PriorityRepository extends JpaRepository<PriorityEntity, Integer> {
    Optional<PriorityEntity> findByPriority(String priority);
}
