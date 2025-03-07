package com.example.serviceutilisateur.models;

import com.example.serviceutilisateur.enumerations.OTPStatus;
import com.example.serviceutilisateur.enumerations.Status;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OTPStatus OTPStatus;

    public static OTP generateOtpForUser(Utilisateur utilisateur) {
        String otpCode = String.format("%06d", (int) (Math.random() * 900000) + 100000);
        LocalDateTime expirationDate = LocalDateTime.now().plusMinutes(10);
        OTP otp = new OTP();
        otp.setCode(otpCode);
        otp.setExpirationDate(expirationDate);
        otp.setUtilisateur(utilisateur);
        return otp;
    }

    public boolean isActive() {
        return LocalDateTime.now().isBefore(this.expirationDate);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public @Size(min = 6, max = 6, message = "Le message OTP doit contenir exactement 6 chiffres.") String getCode() {
        return code;
    }

    public void setCode(@Size(min = 6, max = 6, message = "Le message OTP doit contenir exactement 6 chiffres.") String code) {
        this.code = code;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public com.example.serviceutilisateur.enumerations.OTPStatus getOTPStatus() {
        return OTPStatus;
    }

    public void setOTPStatus(com.example.serviceutilisateur.enumerations.OTPStatus OTPStatus) {
        this.OTPStatus = OTPStatus;
    }
}
