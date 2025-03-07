package com.example.serviceutilisateur.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "otps")
@NoArgsConstructor
@AllArgsConstructor
public class OTP {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Size(min = 6, max = 6, message = "Le message OTP doit contenir exactement 6 chiffres.")
    private String code;

    @Column(name = "expiration_date", nullable = false)
    private LocalDateTime expirationDate;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    public static OTP generateOtpForUser(Utilisateur utilisateur) {
        String otpCode = String.format("%06d", (int) (Math.random() * 900000) + 100000);
        LocalDateTime expirationDate = LocalDateTime.now().plusMinutes(10);
        return new OTP(0, otpCode, expirationDate, utilisateur);
    }

    public boolean isActive() {
        return LocalDateTime.now().isBefore(this.expirationDate);
    }
}
