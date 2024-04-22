package com.ditod.notes.domain.note;

import com.ditod.notes.domain.DateTimeAudit;
import com.ditod.notes.domain.note_image.NoteImage;
import com.ditod.notes.domain.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Note extends DateTimeAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @NotNull
    private String title;
    @NotNull
    private String content;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "ownerId")
    @JsonBackReference
    private User owner;
    @NotNull
    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<NoteImage> images = new ArrayList<>();

    public Note() {
    }

    public Note(UUID id, String title, String content, User owner) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.owner = owner;
    }

    public Note(String title, String content, User owner) {
        this.title = title;
        this.content = content;
        this.owner = owner;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public User getOwner() {
        return owner;
    }

    public List<NoteImage> getImages() {
        return images;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setImages(List<NoteImage> images) {
        this.images = images;
    }
}
