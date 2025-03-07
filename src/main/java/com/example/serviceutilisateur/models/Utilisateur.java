package com.example.serviceutilisateur.models;

import com.example.serviceutilisateur.enumerations.Status;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "utilisateurs")
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Le pseudo est obligatoire.")
    @Size(max = 100, min = 5, message = "Le pseudo doit avoir entre 05 et 100 caractères.")
    @Column(unique = true)
    private String pseudo;

    @NotBlank(message = "Le mot de passe est obligatoire.")
    private String motDePasse;

    @Email(message = "L'email doit être valide.")
    @NotBlank(message = "L'email est obligatoire.")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Le prénom est obligatoire.")
    @Size(max = 100, message = "Le prénom ne doit pas dépasser 100 caractères.")
    private String prenom;

    @NotBlank(message = "Le nom est obligatoire.")
    @Size(max = 100, message = "Le nom doit comprendre entre 05 et 100 caractères.")
    private String nom;

    private String image;

    private String phone;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    @JsonBackReference
    private Role role;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    @JsonBackReference
    private Utilisateur createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "updated_by")
    @JsonBackReference
    private Utilisateur updatedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OTP> otps = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        status = Status.EN_ATTENTE;
        OTP newOtp = OTP.generateOtpForUser(this);
        otps.add(newOtp);
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public OTP getActiveOtp() {
        return otps.stream()
                .filter(OTP::isActive)
                .findFirst()
                .orElse(null);
    }

    public void activateAccount() {
        if (this.status == Status.EN_ATTENTE) {
            this.status = Status.ACTIF;
        }
    }
}
