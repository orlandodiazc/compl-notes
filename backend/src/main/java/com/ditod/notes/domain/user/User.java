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

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User extends DateTimeAudit {
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

    @NotNull
    private String name;

    @JsonManagedReference
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Note> notes;

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "image_id", referencedColumnName = "id")
    private UserImage image;

    @NotNull
    @ManyToMany
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<Role> roles;

    public User() {
    }

    public User(String email, String username, String password, String name, List<Role> roles,
                UserImage userImage) {
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

    public void setId(UUID id) {
        this.id = id;
    }

    public @NotNull String getEmail() {
        return email;
    }

    public void setEmail(@NotNull String email) {
        this.email = email;
    }

    public @NotNull String getUsername() {
        return username;
    }

    public void setUsername(@NotNull String username) {
        this.username = username;
    }

    public @NotNull String getPassword() {
        return password;
    }

    public void setPassword(@NotNull String password) {
        this.password = password;
    }

    public @NotNull String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
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

    public @NotNull List<Role> getRoles() {
        return roles;
    }

    public void setRoles(
            @NotNull List<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", email='" + email + '\'' + ", username='" + username + '\'' + ", password='" + password + '\'' + ", name='" + name + '\'' + ", image=" + image + '}';
    }
}
