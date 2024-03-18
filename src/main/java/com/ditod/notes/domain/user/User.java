package com.ditod.notes.domain.user;

import com.ditod.notes.domain.DateTimeAudit;
import com.ditod.notes.domain.note.Note;
import com.ditod.notes.domain.user_image.UserImage;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User extends DateTimeAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String username;

    private String name;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Note> notes;
    @JsonManagedReference
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserImage image;

    public User() {
    }

    public User(String email, String username, String name, List<Note> notes) {
        this.email = email;
        this.username = username;
        this.name = name;
        this.notes = notes;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public UserImage getImage() {
        return image;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    public void setImage(UserImage image) {
        this.image = image;
    }
}
