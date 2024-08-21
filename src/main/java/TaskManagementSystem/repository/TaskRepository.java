package TaskManagementSystem.repository;

import TaskManagementSystem.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Integer> {
    @Query(value = "select t.*\n" +
            "from tasks t \n" +
            "left join accounts a\n" +
            "on t.author_id = a.account_id or t.executor_id = a.account_id \n" +
            "left join statuses s \n" +
            "using (status_id)\n" +
            "left join priorities p \n" +
            "using (priority_id)\n" +
            "where a.account_id = :accountId\n" +
            "and (:status = '' or s.status = :status)\n" +
            "and (:priority = '' or p.priority = :priority)", nativeQuery = true)
    List<TaskEntity> findTasksByAccountIdAndFilters(
            @Param("accountId") Integer accountId,
            @Param("status") String status,
            @Param("priority") String priority
    );
}
