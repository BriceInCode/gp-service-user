package com.example.serviceutilisateur.implementations;

import com.example.serviceutilisateur.enumerations.OTPStatus;
import com.example.serviceutilisateur.models.OTP;
import com.example.serviceutilisateur.models.Utilisateur;
import com.example.serviceutilisateur.repositories.OTPRepository;
import com.example.serviceutilisateur.repositories.UtilisateurRepository;
import com.example.serviceutilisateur.responses.ResponseDTO;
import com.example.serviceutilisateur.services.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OtpServiceImpl implements OtpService {

    @Autowired
    private OTPRepository otpRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Override
    public boolean generateOtp(Long utilisateurId) {
        Optional<Utilisateur> utilisateurOptional = utilisateurRepository.findById(utilisateurId);
        if (utilisateurOptional.isPresent()) {
            Utilisateur utilisateur = utilisateurOptional.get();
            List<OTP> existingOtps = otpRepository.findByUtilisateur(utilisateur);
            if (existingOtps != null && !existingOtps.isEmpty()) {
                for (OTP otp : existingOtps) {
                    if (!otp.getOTPStatus().equals(OTPStatus.EXPIRER)) {
                        otp.setOTPStatus(OTPStatus.EXPIRER);
                        otpRepository.save(otp);
                    }
                }
            }
            OTP newOtp = OTP.generateOtpForUser(utilisateur);
            newOtp.setOTPStatus(OTPStatus.ACTIF);
            newOtp.setExpirationDate(LocalDateTime.now().plusMinutes(15));
            OTP savedOtp = otpRepository.save(newOtp);
            return savedOtp != null;
        } else {
            return false;
        }
    }




    @Override
    public boolean verifyOtp(Long utilisateurId, String otpCode) {
        Optional<Utilisateur> utilisateurOptional = utilisateurRepository.findById(utilisateurId);
        if (utilisateurOptional.isPresent()) {
            Utilisateur utilisateur = utilisateurOptional.get();
            List<OTP> otps = otpRepository.findByUtilisateur(utilisateur);
            OTP activeOtp = otps.stream()
                    .filter(otp -> otp.getOTPStatus().equals(OTPStatus.ACTIF) && otp.getExpirationDate().isAfter(LocalDateTime.now()))
                    .findFirst()
                    .orElse(null);

            if (activeOtp != null && activeOtp.getCode().equals(otpCode)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteExpiredOtps(Long utilisateurId) {
        Optional<Utilisateur> utilisateurOptional = utilisateurRepository.findById(utilisateurId);
        if (utilisateurOptional.isPresent()) {
            Utilisateur utilisateur = utilisateurOptional.get();

            List<OTP> expiredOtps = otpRepository.findByUtilisateur(utilisateur).stream()
                    .filter(otp -> otp.getExpirationDate().isBefore(LocalDateTime.now()) || otp.getOTPStatus().equals(OTPStatus.EXPIRER))
                    .collect(Collectors.toList());

            if (!expiredOtps.isEmpty()) {
                otpRepository.deleteAll(expiredOtps);
                return true;
            }
        }
        return false;
    }

}
