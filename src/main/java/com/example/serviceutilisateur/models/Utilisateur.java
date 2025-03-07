package com.example.serviceutilisateur.models;

import com.example.serviceutilisateur.enumerations.Status;
import com.example.serviceutilisateur.validations.ValidPhoneNumber;
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

    @NotBlank(message = "Le numéro du destinataire ne peut pas être vide.")
    @ValidPhoneNumber(message = "Le numéro de téléphone du destinataire doit être un numéro camerounais valide.")
    @Column(nullable = false)
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public @NotBlank(message = "Le pseudo est obligatoire.") @Size(max = 100, min = 5, message = "Le pseudo doit avoir entre 05 et 100 caractères.") String getPseudo() {
        return pseudo;
    }

    public void setPseudo(@NotBlank(message = "Le pseudo est obligatoire.") @Size(max = 100, min = 5, message = "Le pseudo doit avoir entre 05 et 100 caractères.") String pseudo) {
        this.pseudo = pseudo;
    }

    public @NotBlank(message = "Le mot de passe est obligatoire.") String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(@NotBlank(message = "Le mot de passe est obligatoire.") String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public @Email(message = "L'email doit être valide.") @NotBlank(message = "L'email est obligatoire.") String getEmail() {
        return email;
    }

    public void setEmail(@Email(message = "L'email doit être valide.") @NotBlank(message = "L'email est obligatoire.") String email) {
        this.email = email;
    }

    public @NotBlank(message = "Le prénom est obligatoire.") @Size(max = 100, message = "Le prénom ne doit pas dépasser 100 caractères.") String getPrenom() {
        return prenom;
    }

    public void setPrenom(@NotBlank(message = "Le prénom est obligatoire.") @Size(max = 100, message = "Le prénom ne doit pas dépasser 100 caractères.") String prenom) {
        this.prenom = prenom;
    }

    public @NotBlank(message = "Le nom est obligatoire.") @Size(max = 100, message = "Le nom doit comprendre entre 05 et 100 caractères.") String getNom() {
        return nom;
    }

    public void setNom(@NotBlank(message = "Le nom est obligatoire.") @Size(max = 100, message = "Le nom doit comprendre entre 05 et 100 caractères.") String nom) {
        this.nom = nom;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Utilisateur getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Utilisateur createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Utilisateur getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Utilisateur updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<OTP> getOtps() {
        return otps;
    }

    public void setOtps(List<OTP> otps) {
        this.otps = otps;
    }
}
