package TaskManagementSystem.entity;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "accounts", schema = "public")
@SequenceGenerator(name = "accounts_account_id_seq", sequenceName = "accounts_account_id_seq", allocationSize = 1)
public class AccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accounts_account_id_seq")
    @Column(name = "account_id")
    private Integer accountId;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "firstname")
    private String firstname;

    @Column(name = "lastname")
    private String lastname;

    @Column(name = "role_id")
    private Integer roleId;

    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "role_id", insertable = false, updatable = false)
    private RoleEntity roleEntity;

    @OneToMany(mappedBy = "authorEntity")
    private Set<TaskEntity> taskEntitiesByAuthor;

    @OneToMany(mappedBy = "executorEntity")
    private Set<TaskEntity> taskEntitiesForExecutor;

    public AccountEntity(){}

    public AccountEntity(String email, String password, String firstname, String lastname, Integer roleId) {
        this.email = email;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.roleId = roleId;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleEntity(RoleEntity roleEntity) {
        this.roleEntity = roleEntity;
    }

    public RoleEntity getRoleEntity() {
        return roleEntity;
    }

    public Set<TaskEntity> getTaskEntitiesByAuthor() {
        return taskEntitiesByAuthor;
    }

    public Set<TaskEntity> getTaskEntitiesForExecutor() {
        return taskEntitiesForExecutor;
    }

    public String getPassword() {
        return password;
    }
}
