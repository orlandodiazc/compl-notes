package com.ditod.notes.domain.note_image;

import com.ditod.notes.domain.DateTimeAudit;
import com.ditod.notes.domain.note.Note;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class NoteImage extends DateTimeAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String altText;
    private String contentType;
    private byte[] blob;

    @ManyToOne
    @JoinColumn(name = "noteId")
    private Note note;

    public NoteImage() {
    }

    public NoteImage(String altText, String contentType, byte[] blob, Note note) {
        this.altText = altText;
        this.contentType = contentType;
        this.blob = blob;
        this.note = note;
    }

    public UUID getId() {
        return id;
    }

    public String getAltText() {
        return altText;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getBlob() {
        return blob;
    }

    public Note getNote() {
        return note;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setBlob(byte[] blob) {
        this.blob = blob;
    }

    public void setNote(Note note) {
        this.note = note;
    }
}

