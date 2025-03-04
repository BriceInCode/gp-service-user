package com.example.serviceutilisateur.models;

import com.example.serviceutilisateur.enumerations.Status;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

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

    @Column(name = "otp")
    @Size(min = 6, max = 6, message = "Le message OTP doit avoir contenir exactement 6 chiffres.")
    private String otp;

    @Column(name = "otp_expiration_date")
    private LocalDateTime otpExpirationDate;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        status = Status.INACTIF;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void generateOtp() {
        this.otp = String.format("%06d", (int) (Math.random() * 900000) + 100000);  // Génère un OTP de 6 chiffres sous forme de String
        this.otpExpirationDate = LocalDateTime.now().plusMinutes(10);
    }

    public boolean verifyOtp(String otpInput) {
        if (this.otp != null && this.otp.equals(otpInput) && LocalDateTime.now().isBefore(this.otpExpirationDate)) {
            return true;
        }
        return false;
    }

    public void activateAccount() {
        if (this.status == Status.INACTIF) {
            this.status = Status.ACTIF;
        }
    }

}
