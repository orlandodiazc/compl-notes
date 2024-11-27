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

    @JsonManagedReference
    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL)
    private List<NoteImage> images;

    public Note() {
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

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<NoteImage> getImages() {
        return images;
    }

    public void setImages(List<NoteImage> images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "Note{" + "title='" + title + '\'' + ", content='" + content + '\'' + ", owner=" + owner + '}';
    }
}
