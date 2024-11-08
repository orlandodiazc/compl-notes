package com.ditod.notes.domain.user;

import com.ditod.notes.domain.DateTimeAudit;
import com.ditod.notes.domain.note.Note;
import com.ditod.notes.domain.role.Role;
import com.ditod.notes.domain.user_image.UserImage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.ColumnTransformer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User extends DateTimeAudit implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    @NotNull
    @ColumnTransformer(write = "LOWER(?)")
    private String email;

    @Column(unique = true)
    @NotNull
    @ColumnTransformer(write = "LOWER(?)")
    private String username;

    @NotNull
    @JsonIgnore
    private String password;

    private String name;

    @NotNull
    @JsonManagedReference
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Note> notes = new ArrayList<>();

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "image_id", referencedColumnName = "id")
    private UserImage image;

    @NotNull
    @ManyToMany
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns =
    @JoinColumn(name = "user_id"))
    private List<Role> roles = new ArrayList<>();

    public User() {
    }

    public User(String email, String username, String password, String name, List<Role> roles, UserImage userImage) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.name = name;
        this.roles = roles;
        this.image = userImage;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    public UserImage getImage() {
        return image;
    }

    public void setImage(UserImage image) {
        this.image = image;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String getPassword() {return this.password;}

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(Role::getPermissions).flatMap(Collection::stream).toList();
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", email='" + email + '\'' + ", username='" + username + '\'' + ", password='" + password + '\'' + ", name='" + name + '\'' + ", image=" + image + '}';
    }
}
