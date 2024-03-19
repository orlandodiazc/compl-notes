package com.ditod.notes.domain.note;

import com.ditod.notes.domain.DateTimeAudit;
import com.ditod.notes.domain.note_image.NoteImage;
import com.ditod.notes.domain.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Note extends DateTimeAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;
    private String content;

    @ManyToOne
    @JoinColumn(name = "ownerId")
    @JsonBackReference
    private User owner;

    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<NoteImage> images;

    public Note() {
    }

    public Note(String title, String content, User owner) {
        this(title, content, owner, new ArrayList<>());
    }

    public Note(String title, String content, User owner, List<NoteImage> images) {
        this.title = title;
        this.content = content;
        this.owner = owner;
        this.images = images;
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
