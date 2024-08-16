package TaskManagementSystem.entity;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "priorities", schema = "public")
@SequenceGenerator(name = "priorities_priority_id_seq", sequenceName = "priorities_priority_id_seq", allocationSize = 1)
public class PriorityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "priorities_priority_id_seq")
    @Column(name = "priority_id")
    private Integer priorityId;

    @Column(name = "priority")
    private String priority;

    @OneToMany(mappedBy = "priorityEntity")
    private Set<TaskEntity> taskEntities;

    public PriorityEntity(){}

    public PriorityEntity(String priority) {
        this.priority = priority;
    }

    public Integer getPriorityId() {
        return priorityId;
    }

    public String getPriority() {
        return priority;
    }

    public Set<TaskEntity> getTaskEntities() {
        return taskEntities;
    }
}
