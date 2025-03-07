package com.example.serviceutilisateur.implementations;

import com.example.serviceutilisateur.enumerations.Status;
import com.example.serviceutilisateur.execptions.ResourceNotFoundException;
import com.example.serviceutilisateur.models.Role;
import com.example.serviceutilisateur.models.Utilisateur;
import com.example.serviceutilisateur.repositories.RoleRepository;
import com.example.serviceutilisateur.repositories.UtilisateurRepository;
import com.example.serviceutilisateur.responses.ResponseDTO;
import com.example.serviceutilisateur.services.OtpService;
import com.example.serviceutilisateur.services.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurServiceImpl implements UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private OtpService otpService;

    private static final String IMAGE_UPLOAD_DIR = "uploads";
    private static final long MAX_IMAGE_SIZE = 4 * 1024 * 1024; // 4 Mo
    private static final String[] ALLOWED_IMAGE_TYPES = {"image/jpeg", "image/png", "image/jpg"};

    private <T> ResponseDTO<T> handleException(Exception e, String action) {
        return new ResponseDTO<>(ResponseDTO.ERROR, "Erreur lors de " + action + ": " + e.getMessage());
    }

    @Override
    public ResponseDTO<Utilisateur> createUtilisateur(Utilisateur utilisateur) {
        try {
            ResponseDTO<String> verificationResponse = verifyUtilisateurExistence(utilisateur);
            if (verificationResponse != null) {
                return new ResponseDTO<>(ResponseDTO.ERROR, verificationResponse.getMessage(), null);
            }
            if (utilisateur.getRole() == null || !roleRepository.existsById(utilisateur.getRole().getId())) {
                return new ResponseDTO<>(ResponseDTO.ERROR, "Le rôle de l'utilisateur est invalide.", null);
            }
            Optional<Role> roleOptional = roleRepository.findById(utilisateur.getRole().getId());
            roleOptional.ifPresentOrElse(utilisateur::setRole, () -> {
                throw new IllegalArgumentException("Le rôle spécifié n'existe pas.");
            });
            initializeUtilisateur(utilisateur);
            Utilisateur savedUtilisateur = utilisateurRepository.save(utilisateur);
            if (savedUtilisateur.getId() == 0) {
                return new ResponseDTO<>(ResponseDTO.ERROR, "Échec de l'enregistrement de l'utilisateur.", null);
            }
            boolean otpGenerated = otpService.generateOtp(savedUtilisateur.getId());
            if (!otpGenerated) {
                return new ResponseDTO<>(ResponseDTO.ERROR, "Erreur lors de la génération de l'OTP.", null);
            }
            return new ResponseDTO<>(ResponseDTO.SUCCESS, "Utilisateur créé avec succès.", savedUtilisateur);
        } catch (Exception e) {
            return handleException(e, "la création de l'utilisateur");
        }
    }

    @Override
    public ResponseDTO<Utilisateur> updateUtilisateur(long id, Utilisateur utilisateur) {
        try {
            Utilisateur existingUtilisateur = utilisateurRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id : " + id));
            if (utilisateur.getPseudo() != null) existingUtilisateur.setPseudo(utilisateur.getPseudo());
            if (utilisateur.getMotDePasse() != null) existingUtilisateur.setMotDePasse(utilisateur.getMotDePasse());
            if (utilisateur.getEmail() != null) existingUtilisateur.setEmail(utilisateur.getEmail());
            if (utilisateur.getPrenom() != null) existingUtilisateur.setPrenom(utilisateur.getPrenom());
            if (utilisateur.getNom() != null) existingUtilisateur.setNom(utilisateur.getNom());
            if (utilisateur.getPhone() != null) existingUtilisateur.setPhone(utilisateur.getPhone());
            if (utilisateur.getRole() != null) existingUtilisateur.setRole(utilisateur.getRole());
            existingUtilisateur.setUpdatedAt(LocalDateTime.now());
            Utilisateur updatedUtilisateur = utilisateurRepository.save(existingUtilisateur);
            return new ResponseDTO<>(ResponseDTO.SUCCESS, "Utilisateur mis à jour avec succès.", updatedUtilisateur);
        } catch (Exception e) {
            return handleException(e, "la mise à jour de l'utilisateur");
        }
    }

    @Override
    public ResponseDTO<Utilisateur> getUtilisateurById(long id) {
        return utilisateurRepository.findById(id)
                .map(u -> new ResponseDTO<>(ResponseDTO.SUCCESS, "Utilisateur trouvé", u))
                .orElseGet(() -> new ResponseDTO<>(ResponseDTO.ERROR, "Utilisateur non trouvé", null));
    }

    @Override
    public ResponseDTO<List<Utilisateur>> getAllUtilisateurs() {
        List<Utilisateur> utilisateurs = utilisateurRepository.findAll();
        return new ResponseDTO<>(ResponseDTO.SUCCESS, "Liste des utilisateurs récupérée", utilisateurs);
    }

    @Override
    public ResponseDTO<String> deleteUtilisateur(long id) {
        try {
            Utilisateur utilisateur = utilisateurRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID : " + id));
            utilisateurRepository.delete(utilisateur);
            return new ResponseDTO<>(ResponseDTO.SUCCESS, "Utilisateur supprimé avec succès", null);
        } catch (Exception e) {
            return handleException(e, "la suppression de l'utilisateur");
        }
    }

    @Override
    public ResponseDTO<String> changeUtilisateurStatus(long id, Status status) {
        try {
            Utilisateur utilisateur = utilisateurRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id : " + id));
            if (utilisateur.getStatus() != status) {
                utilisateur.setStatus(status);
                utilisateurRepository.save(utilisateur);
                return new ResponseDTO<>(ResponseDTO.SUCCESS, "Statut de l'utilisateur mis à jour", null);
            } else {
                return new ResponseDTO<>(ResponseDTO.WARNING, "Le statut est déjà le même", null);
            }
        } catch (Exception e) {
            return handleException(e, "le changement de statut de l'utilisateur");
        }
    }

    @Override
    public ResponseDTO<String> resetPassword(String email, String newPassword) {
        try {
            Utilisateur utilisateur = utilisateurRepository.findByEmail(email);
            if (utilisateur == null) {
                return new ResponseDTO<>(ResponseDTO.ERROR, "Utilisateur avec l'email " + email + " non trouvé.", null);
            }
            if (utilisateur.getMotDePasse().equals(newPassword)) {
                return new ResponseDTO<>(ResponseDTO.WARNING, "Le nouveau mot de passe est identique à l'ancien. Aucun changement effectué.", null);
            }
            utilisateur.setMotDePasse(newPassword);
            utilisateurRepository.save(utilisateur);
            return new ResponseDTO<>(ResponseDTO.SUCCESS, "Mot de passe réinitialisé avec succès.", null);
        } catch (Exception e) {
            return handleException(e, "la réinitialisation du mot de passe");
        }
    }

    @Override
    public ResponseDTO<Utilisateur> updateProfileImage(long id, MultipartFile newImageFile) {
        try {
            Utilisateur utilisateur = utilisateurRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id : " + id));

            if (utilisateur.getImage() != null) {
                deleteImage(utilisateur.getImage());
            }
            String newImageUrl = handleImageUpload(newImageFile, "user-", utilisateur.getImage());
            utilisateur.setImage(newImageUrl);
            utilisateurRepository.save(utilisateur);
            return new ResponseDTO<>(ResponseDTO.SUCCESS, "Photo de profil mise à jour avec succès.", utilisateur);
        } catch (IOException e) {
            return handleException(e, "l'upload de la nouvelle image");
        }
    }

    private String generateDefaultPassword() {
        return "changeme@" + Year.now().getValue();
    }

    private void initializeUtilisateur(Utilisateur utilisateur) {
        utilisateur.setCreatedAt(LocalDateTime.now());
        utilisateur.setMotDePasse(generateDefaultPassword());
        utilisateur.setStatus(Status.EN_ATTENTE);
    }

    private ResponseDTO<String> verifyUtilisateurExistence(Utilisateur utilisateur) {
        if (utilisateurRepository.existsByEmail(utilisateur.getEmail())) {
            return new ResponseDTO<>(ResponseDTO.ERROR, "Un utilisateur avec cet email existe déjà.", null);
        }
        if (utilisateurRepository.existsByPseudo(utilisateur.getPseudo())) {
            return new ResponseDTO<>(ResponseDTO.ERROR, "Un utilisateur avec ce pseudo existe déjà.", null);
        }
        if (utilisateurRepository.existsByPhone(utilisateur.getPhone())) {
            return new ResponseDTO<>(ResponseDTO.ERROR, "Un utilisateur avec ce numéro de téléphone existe déjà.", null);
        }
        return null;
    }

    private void deleteImage(String imageUrl) {
        File existingImage = new File(imageUrl);
        if (existingImage.exists()) {
            existingImage.delete();
        }
    }

    public String handleImageUpload(MultipartFile imageFile, String prefix, String existingImageUrl) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            if (imageFile.getSize() > MAX_IMAGE_SIZE) {
                throw new IOException("La taille de l'image dépasse la limite de 4 Mo.");
            }

            String fileType = imageFile.getContentType();
            if (!isAllowedImageType(fileType)) {
                throw new IOException("Type de fichier non autorisé. Seules les images jpg, jpeg et png sont acceptées.");
            }

            String timestamp = String.valueOf(System.currentTimeMillis());
            String newFileName = prefix + timestamp + "-" + imageFile.getOriginalFilename();
            Path path = Paths.get(IMAGE_UPLOAD_DIR + newFileName);
            Files.createDirectories(path.getParent());
            Files.copy(imageFile.getInputStream(), path);
            return path.toString();
        }
        return existingImageUrl;
    }

    private boolean isAllowedImageType(String fileType) {
        for (String allowedType : ALLOWED_IMAGE_TYPES) {
            if (allowedType.equalsIgnoreCase(fileType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ResponseDTO<Utilisateur> getUtilisateurByEmail(String email) {
        try {
            Utilisateur utilisateur = utilisateurRepository.findByEmail(email);

            if (utilisateur != null) {
                return new ResponseDTO<>(ResponseDTO.SUCCESS, "Utilisateur trouvé", utilisateur);
            } else {
                return new ResponseDTO<>(ResponseDTO.ERROR, "Aucun utilisateur trouvé avec cet email", null);
            }
        } catch (Exception e) {
            return handleException(e, "recherche de l'utilisateur par email");
        }
    }
}
