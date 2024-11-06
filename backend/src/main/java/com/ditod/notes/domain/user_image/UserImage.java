package com.ditod.notes.domain.user_image;

import com.ditod.notes.domain.DateTimeAudit;
import com.ditod.notes.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.UUID;

@Entity
public class UserImage extends DateTimeAudit implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String altText;
    @NotNull
    private String contentType;
    @NotNull
    private byte[] blob;

    @OneToOne(mappedBy = "image")
    private User user;

    public UserImage() {
    }

    public UserImage(String altText, String contentType, @NotNull byte[] blob, User user) {
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "UserImage{" + "id=" + id + ", altText='" + altText + '\'' + ", contentType='" + contentType + '\'' + '}';
    }
}
