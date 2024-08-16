package TaskManagementSystem.entity;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "roles", schema = "public")
@SequenceGenerator(name = "roles_role_id_seq", sequenceName = "roles_role_id_seq", allocationSize = 1)
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roles_role_id_seq")
    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "role")
    private String role;

    @OneToMany(mappedBy = "roleEntity")
    private Set<AccountEntity> accountEntities;

    public RoleEntity(){}

    public RoleEntity(String role) {
        this.role = role;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public String getRole() {
        return role;
    }

    public Set<AccountEntity> getAccountEntities() {
        return accountEntities;
    }
}
