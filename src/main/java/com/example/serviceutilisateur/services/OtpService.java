package com.example.serviceutilisateur.services;

import com.example.serviceutilisateur.models.OTP;
import com.example.serviceutilisateur.models.Utilisateur;
import com.example.serviceutilisateur.responses.ResponseDTO;

import java.util.List;

public interface OtpService {
    boolean generateOtp(Long utilisateurId);
    public boolean verifyOtp(Long utilisateurId, String otpCode);
    public boolean deleteExpiredOtps(Long utilisateurId);
}
