package com.example.serviceutilisateur.implementations;

import com.example.serviceutilisateur.enumerations.Status;
import com.example.serviceutilisateur.execptions.ResourceNotFoundException;
import com.example.serviceutilisateur.models.Utilisateur;
import com.example.serviceutilisateur.repositories.UtilisateurRepository;
import com.example.serviceutilisateur.responses.ResponseDTO;
import com.example.serviceutilisateur.services.UtilisateurService;
import com.example.serviceutilisateur.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Year;
import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurServiceImpl implements UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private RoleService roleService;

    private static final String IMAGE_UPLOAD_DIR = "/path/to/upload/directory/";
    private static final long MAX_IMAGE_SIZE = 4 * 1024 * 1024; // 4 Mo
    private static final String[] ALLOWED_IMAGE_TYPES = {"image/jpeg", "image/png", "image/jpg"};

    // Méthode générique pour gérer les erreurs
    private <T> ResponseDTO<T> handleException(Exception e, String action, T data) {
        return new ResponseDTO<>(ResponseDTO.ERROR, "Erreur lors de " + action + ": " + e.getMessage(), data);
    }

    @Override
    public ResponseDTO<Utilisateur> createUtilisateur(Utilisateur utilisateur, MultipartFile imageFile) {
        try {
            if (utilisateurExistsByEmail(utilisateur.getEmail())) {
                return new ResponseDTO<>(ResponseDTO.ERROR, "L'email est déjà utilisé", null);
            }
            if (utilisateurExistsByPseudo(utilisateur.getPseudo())) {
                return new ResponseDTO<>(ResponseDTO.ERROR, "Le pseudo est déjà utilisé", null);
            }

            utilisateur.setMotDePasse(generateDefaultPassword());
            String imageUrl = handleImageUpload(imageFile, "user-", null);
            utilisateur.setImage(imageUrl);
            utilisateur.setStatus(Status.INACTIF);

            Utilisateur savedUtilisateur = utilisateurRepository.save(utilisateur);
            return new ResponseDTO<>(ResponseDTO.CREATED, "Utilisateur créé avec succès", savedUtilisateur);
        } catch (IOException e) {
            return handleException(e, "l'upload de l'image", null);
        }
    }

    @Override
    public ResponseDTO<Utilisateur> updateUtilisateur(long id, Utilisateur utilisateur, MultipartFile imageFile) {
        try {
            Utilisateur existingUtilisateur = utilisateurRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id : " + id));

            // Validation de l'email et du pseudo
            if (!existingUtilisateur.getEmail().equals(utilisateur.getEmail()) && utilisateurExistsByEmail(utilisateur.getEmail())) {
                return new ResponseDTO<>(ResponseDTO.ERROR, "L'email est déjà utilisé", null);
            }
            if (!existingUtilisateur.getPseudo().equals(utilisateur.getPseudo()) && utilisateurExistsByPseudo(utilisateur.getPseudo())) {
                return new ResponseDTO<>(ResponseDTO.ERROR, "Le pseudo est déjà utilisé", null);
            }

            // Gestion de l'image
            String newImageUrl = handleImageUpload(imageFile, "user-", existingUtilisateur.getImage());
            utilisateur.setImage(newImageUrl);

            // Mise à jour des autres propriétés
            existingUtilisateur.setNom(utilisateur.getNom());
            existingUtilisateur.setPrenom(utilisateur.getPrenom());
            existingUtilisateur.setEmail(utilisateur.getEmail());
            existingUtilisateur.setPseudo(utilisateur.getPseudo());
            existingUtilisateur.setUpdatedAt(utilisateur.getUpdatedAt());
            existingUtilisateur.setStatus(utilisateur.getStatus());

            Utilisateur updatedUtilisateur = utilisateurRepository.save(existingUtilisateur);
            return new ResponseDTO<>(ResponseDTO.SUCCESS, "Utilisateur mis à jour avec succès", updatedUtilisateur);
        } catch (IOException e) {
            return handleException(e, "l'upload de l'image", null);
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
            return handleException(e, "suppression de l'utilisateur", null);
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
            return handleException(e, "changement de statut de l'utilisateur", null);
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
            return handleException(e, "réinitialisation du mot de passe", null);
        }
    }

    @Override
    public ResponseDTO<Utilisateur> changeProfileImage(long id, MultipartFile newImageFile) {
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
            return handleException(e, "l'upload de la nouvelle image", null);
        }
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
            return handleException(e, "recherche de l'utilisateur par email", null);
        }
    }


    // Méthode de génération de mot de passe par défaut
    private String generateDefaultPassword() {
        return "changeme@" + Year.now().getValue();
    }

    private boolean utilisateurExistsByEmail(String email) {
        return utilisateurRepository.existsByEmail(email);
    }

    private boolean utilisateurExistsByPseudo(String pseudo) {
        return utilisateurRepository.existsByPseudo(pseudo);
    }

    // Méthode pour gérer l'upload d'images
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
            Files.copy(imageFile.getInputStream(), path);

            return path.toString();
        }
        return existingImageUrl;
    }

    private boolean isAllowedImageType(String fileType) {
        for (String allowedType : ALLOWED_IMAGE_TYPES) {
            if (allowedType.equals(fileType)) {
                return true;
            }
        }
        return false;
    }

    private void deleteImage(String imageUrl) {
        File existingImage = new File(imageUrl);
        if (existingImage.exists()) {
            existingImage.delete();
        }
    }
}
