package com.example.serviceutilisateur.services;

import com.example.serviceutilisateur.enumerations.Status;
import com.example.serviceutilisateur.models.Utilisateur;
import com.example.serviceutilisateur.responses.ResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UtilisateurService {
    ResponseDTO<Utilisateur> createUtilisateur(Utilisateur utilisateur);
    ResponseDTO<Utilisateur> updateUtilisateur(long id, Utilisateur utilisateur);
    ResponseDTO<Utilisateur> getUtilisateurById(long id);
    ResponseDTO<List<Utilisateur>> getAllUtilisateurs();
    ResponseDTO<String> deleteUtilisateur(long id);
    ResponseDTO<String> changeUtilisateurStatus(long id, Status status);
    ResponseDTO<String> resetPassword(String email, String newPassword);
    ResponseDTO<Utilisateur> updateProfileImage(long id, MultipartFile newImageFile);
    String handleImageUpload(MultipartFile imageFile, String prefix, String existingImageUrl) throws IOException;
    ResponseDTO<Utilisateur> getUtilisateurByEmail(String email);
}
