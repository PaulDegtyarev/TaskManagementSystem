package TaskManagementSystem.entity;

import TaskManagementSystem.dto.dbo.GeneralTaskDBO;
import TaskManagementSystem.dto.dbo.TaskDBOToUpdateTaskByTaskId;
import jakarta.persistence.*;

@Entity
@Table(name = "tasks", schema = "public")
@SequenceGenerator(name = "tasks_task_id_seq", sequenceName = "tasks_task_id_seq", allocationSize = 1)
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tasks_task_id_seq")
    @Column(name = "task_id")
    private Integer taskId;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "status_id")
    private Integer statusId;

    @Column(name = "priority_id")
    private Integer priorityId;

    @Column(name = "author_id")
    private Integer authorId;

    @Column(name = "executor_id")
    private Integer executorId;

    @Column(name = "comment")
    private String comment;

    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "account_id", insertable = false, updatable = false)
    private AccountEntity authorEntity;

    @ManyToOne
    @JoinColumn(name = "executor_id", referencedColumnName = "account_id", insertable = false, updatable = false)
    private AccountEntity executorEntity;

    @ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "status_id", insertable = false, updatable = false)
    private StatusEntity statusEntity;

    @ManyToOne
    @JoinColumn(name = "priority_id", referencedColumnName = "priority_id", insertable = false, updatable = false)
    private PriorityEntity priorityEntity;

    public TaskEntity(){}

    public TaskEntity(String title, String description, Integer statusId, Integer priorityId, Integer authorId, Integer executorId, String comment) {
        this.title = title;
        this.description = description;
        this.statusId = statusId;
        this.priorityId = priorityId;
        this.authorId = authorId;
        this.executorId = executorId;
        this.comment = comment;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public Integer getPriorityId() {
        return priorityId;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public Integer getExecutorId() {
        return executorId;
    }

    public String getComment() {
        return comment;
    }

    public void setAuthorEntity(AccountEntity authorEntity) {
        this.authorEntity = authorEntity;
    }

    public AccountEntity getAuthorEntity() {
        return authorEntity;
    }

    public void setExecutorEntity(AccountEntity executorEntity) {
        this.executorEntity = executorEntity;
    }

    public AccountEntity getExecutorEntity() {
        return executorEntity;
    }

    public StatusEntity getStatusEntity() {
        return statusEntity;
    }

    public PriorityEntity getPriorityEntity() {
        return priorityEntity;
    }

    public void setEntities(AccountEntity authorEntity,
                            AccountEntity executorEntity,
                            StatusEntity statusEntity,
                            PriorityEntity priorityEntity) {
        this.authorEntity = authorEntity;
        this.executorEntity = executorEntity;
        this.statusEntity = statusEntity;
        this.priorityEntity = priorityEntity;
    }

    public void updateTaskEntity(TaskDBOToUpdateTaskByTaskId dto, AccountEntity authorEntity, AccountEntity executorEntity, PriorityEntity priorityEntity, StatusEntity statusEntity) {
        this.title = dto.getTitle();
        this.description = dto.getDescription();
        this.priorityId = priorityEntity.getPriorityId();
        this.authorId = authorEntity.getAccountId();
        this.executorId = executorEntity.getAccountId();
        this.priorityId = priorityEntity.getPriorityId();
        this.statusId = statusEntity.getStatusId();
        this.comment = dto.getComment();
        this.priorityEntity = priorityEntity;
        this.statusEntity = statusEntity;
        this.authorEntity = authorEntity;
        this.executorEntity = executorEntity;
    }

    public void updateStatus(StatusEntity statusEntity) {
        this.statusId = statusEntity.getStatusId();
        this.statusEntity = statusEntity;
    }

    public void updateExecutor(AccountEntity executorEntity) {
        this.executorId = executorEntity.getAccountId();
        this.executorEntity = executorEntity;
    }
}