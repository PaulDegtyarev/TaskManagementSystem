package TaskManagementSystem.entity;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "statuses", schema = "public")
@SequenceGenerator(name = "statuses_status_id_seq", sequenceName = "statuses_status_id_seq", allocationSize = 1)
public class StatusEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "statuses_status_id_seq")
    @Column(name = "status_id")
    private Integer statusId;

    @Column(name = "status")
    private String status;

    @OneToMany(mappedBy = "statusEntity")
    private Set<TaskEntity> taskEntities;

    public StatusEntity(){}

    public StatusEntity(String status) {
        this.status = status;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public String getStatus() {
        return status;
    }

    public Set<TaskEntity> getTaskEntities() {
        return taskEntities;
    }
}