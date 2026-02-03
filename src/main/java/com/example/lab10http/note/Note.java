package com.example.lab10http.note;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "notes")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false, updatable = false)
    private String ownerUsername;

    protected Note() { }

    public Note(String content, String ownerUsername) {
        this.content = content;
        this.ownerUsername = ownerUsername;
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
