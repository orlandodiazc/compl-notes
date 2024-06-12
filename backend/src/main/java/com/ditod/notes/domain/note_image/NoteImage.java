package com.ditod.notes.domain.note_image;

import com.ditod.notes.domain.DateTimeAudit;
import com.ditod.notes.domain.note.Note;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
public class NoteImage extends DateTimeAudit {
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String altText;
    @NotNull
    @JsonIgnore
    private String contentType;
    @NotNull
    @JsonIgnore
    private byte[] blob;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "noteId")
    @JsonBackReference
    private Note note;

    public NoteImage() {
    }

    public NoteImage(String altText, String contentType, byte[] blob,
            Note note) {
        this.altText = altText;
        this.contentType = contentType;
        this.blob = blob;
        this.note = note;
    }

    public NoteImage(UUID id, String altText, String contentType, byte[] blob,
            Note note) {
        this.id = id;
        this.altText = altText;
        this.contentType = contentType;
        this.blob = blob;
        this.note = note;
    }

    public NoteImage(UUID id, String altText, Note note) {
        this.id = id;
        this.altText = altText;
        this.note = note;
    }


    public UUID getId() {
        return id;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getBlob() {
        return blob;
    }

    public void setBlob(byte[] blob) {
        this.blob = blob;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }
}

