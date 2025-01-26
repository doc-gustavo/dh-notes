package com.dh.notesapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título no puede estar vacío")
    @Size(max = 100, message = "El título no puede exceder los 100 caracteres")
    private String title;

    @NotBlank(message = "El contenido no puede estar vacío")
    @Size(max = 1000, message = "El contenido no puede exceder los 1000 caracteres")
    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
