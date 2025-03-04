package com.example.serviceutilisateur.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "permissions")
@NoArgsConstructor
@AllArgsConstructor
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Le nom de la permission est obligatoire.")
    @Size(max = 100, min = 5, message = "Le nom doit avoir entre 05 et 100 caractères.")
    private String nom;

    @NotBlank(message = "La description est obligatoire.")
    @Size(max = 1000, min = 5, message = "La description doit avoir entre 05 et 1000 caractères.")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public @NotBlank(message = "Le nom de la permission est obligatoire.") @Size(max = 100, min = 5, message = "Le nom doit avoir entre 05 et 100 caractères.") String getNom() {
        return nom;
    }

    public void setNom(@NotBlank(message = "Le nom de la permission est obligatoire.") @Size(max = 100, min = 5, message = "Le nom doit avoir entre 05 et 100 caractères.") String nom) {
        this.nom = nom;
    }

    public @NotBlank(message = "La description est obligatoire.") @Size(max = 1000, min = 5, message = "La description doit avoir entre 05 et 1000 caractères.") String getDescription() {
        return description;
    }

    public void setDescription(@NotBlank(message = "La description est obligatoire.") @Size(max = 1000, min = 5, message = "La description doit avoir entre 05 et 1000 caractères.") String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
