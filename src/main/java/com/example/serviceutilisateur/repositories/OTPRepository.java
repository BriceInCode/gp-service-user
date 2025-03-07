package com.example.serviceutilisateur.repositories;

import com.example.serviceutilisateur.models.OTP;
import com.example.serviceutilisateur.models.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OTPRepository extends JpaRepository<OTP, Long> {
    List<OTP> findByUtilisateur(Utilisateur utilisateur);
}
