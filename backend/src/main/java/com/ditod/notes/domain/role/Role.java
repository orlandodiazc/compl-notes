package com.ditod.notes.domain.role;

import com.ditod.notes.domain.DateTimeAudit;
import com.ditod.notes.domain.permission.Permission;
import com.ditod.notes.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Entity
public class Role extends DateTimeAudit implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(unique = true)
    private String name;

    @NotNull
    private String description = "";

    @ManyToMany(mappedBy = "roles")
    private List<User> users;

    @NotNull
    @ManyToMany
    @JoinTable(name = "role_permission", joinColumns = @JoinColumn(name = "permission_id"), inverseJoinColumns =
    @JoinColumn(name = "role_id"))
    private List<Permission> permissions;

    public Role() {
    }

    public Role(String name, List<Permission> permissions) {
        this.name = name;
        this.permissions = permissions;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", permissions=" + permissions +
                '}';
    }
}