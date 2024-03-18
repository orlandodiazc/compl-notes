package com.ditod.notes.domain.user_image;

import com.ditod.notes.domain.DateTimeAudit;
import com.ditod.notes.domain.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class UserImage extends DateTimeAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String altText;
    private String contentType;
    private byte[] blob;

    @OneToOne
    @JoinColumn(name = "userId")
    @JsonBackReference
    private User user;

    public UserImage() {
    }

    public UserImage(String altText, String contentType, byte[] blob, User user) {
        this.altText = altText;
        this.contentType = contentType;
        this.blob = blob;
        this.user = user;
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

    public User getUser() {
        return user;
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

    public void setUser(User user) {
        this.user = user;
    }
}
