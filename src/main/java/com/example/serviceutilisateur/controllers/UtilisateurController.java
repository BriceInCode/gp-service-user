package com.example.serviceutilisateur.controllers;

import com.example.serviceutilisateur.models.Utilisateur;
import com.example.serviceutilisateur.responses.ResponseDTO;
import com.example.serviceutilisateur.services.UtilisateurService;
import com.example.serviceutilisateur.enumerations.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;

    @Operation(summary = "Obtenir tous les utilisateurs")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des utilisateurs récupérée avec succès"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping
    public ResponseDTO<List<Utilisateur>> getAllUtilisateurs() {
        return utilisateurService.getAllUtilisateurs();
    }

    @Operation(summary = "Obtenir un utilisateur par ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur récupéré avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    @GetMapping("/{id}")
    public ResponseDTO<Utilisateur> getUtilisateurById(@PathVariable Long id) {
        return utilisateurService.getUtilisateurById(id);
    }

    @Operation(summary = "Obtenir un utilisateur par email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur récupéré avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé avec cet email")
    })
    @GetMapping("/email/{email}")
    public ResponseDTO<Utilisateur> getUtilisateurByEmail(@PathVariable String email) {
        return utilisateurService.getUtilisateurByEmail(email);
    }

    @Operation(summary = "Créer un nouvel utilisateur")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Utilisateur créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Requête invalide")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDTO<Utilisateur> createUtilisateur(@RequestBody Utilisateur utilisateur, @RequestParam("image") MultipartFile imageFile) {
        return utilisateurService.createUtilisateur(utilisateur, imageFile);
    }

    @Operation(summary = "Mettre à jour un utilisateur existant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
            @ApiResponse(responseCode = "400", description = "Requête invalide")
    })
    @PutMapping("/{id}")
    public ResponseDTO<Utilisateur> updateUtilisateur(@PathVariable Long id, @RequestBody Utilisateur utilisateur, @RequestParam("image") MultipartFile imageFile) {
        return utilisateurService.updateUtilisateur(id, utilisateur, imageFile);
    }

    @Operation(summary = "Supprimer un utilisateur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    @DeleteMapping("/{id}")
    public ResponseDTO<String> deleteUtilisateur(@PathVariable Long id) {
        return utilisateurService.deleteUtilisateur(id);
    }

    @Operation(summary = "Changer le statut d'un utilisateur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Statut de l'utilisateur modifié avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
            @ApiResponse(responseCode = "400", description = "Transition de statut invalide")
    })
    @PutMapping("/{id}/status")
    public ResponseDTO<String> changeUtilisateurStatus(@PathVariable Long id, @RequestBody Status status) {
        return utilisateurService.changeUtilisateurStatus(id, status);
    }

    @Operation(summary = "Réinitialiser le mot de passe de l'utilisateur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mot de passe réinitialisé avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    @PutMapping("/{email}/reset-password")
    public ResponseDTO<String> resetPassword(@PathVariable String email, @RequestBody String newPassword) {
        return utilisateurService.resetPassword(email, newPassword);
    }

    @Operation(summary = "Changer la photo de profil d'un utilisateur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Photo de profil mise à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
            @ApiResponse(responseCode = "400", description = "Erreur lors de l'upload de l'image")
    })
    @PutMapping("/{id}/profile-image")
    public ResponseDTO<Utilisateur> changeProfileImage(@PathVariable Long id, @RequestParam("image") MultipartFile newImageFile) {
        return utilisateurService.changeProfileImage(id, newImageFile);
    }
}
