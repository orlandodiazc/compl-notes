package com.ditod.notes.domain.permission;

import com.ditod.notes.domain.DateTimeAudit;
import com.ditod.notes.domain.role.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.UUID;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"action", "entity", "access"}))
public class Permission extends DateTimeAudit implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    private String action;

    @NotNull
    private String entity;

    @NotNull
    private String access;

    @NotNull
    private String description = "";

    @ManyToMany(mappedBy = "permissions")
    private List<Role> roles;

    public Permission() {
    }

    public Permission(String action, String entity, String access) {
        this.action = action;
        this.entity = entity;
        this.access = access;
    }

    public UUID getId() {
        return id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String getAuthority() {
        return action + ":" + entity + ":" + access;
    }

    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", action='" + action + '\'' +
                ", entity='" + entity + '\'' +
                ", access='" + access + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}