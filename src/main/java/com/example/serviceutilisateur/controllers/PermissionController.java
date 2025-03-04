package com.example.serviceutilisateur.controllers;

import com.example.serviceutilisateur.models.Permission;
import com.example.serviceutilisateur.responses.ResponseDTO;
import com.example.serviceutilisateur.services.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @Operation(summary = "Obtenir toutes les permissions")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des permissions récupérée avec succès"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping
    public ResponseDTO<List<Permission>> getAllPermissions() {
        return permissionService.getAllPermissions();
    }

    @Operation(summary = "Obtenir une permission par ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Permission récupérée avec succès"),
            @ApiResponse(responseCode = "404", description = "Permission non trouvée")
    })
    @GetMapping("/{id}")
    public ResponseDTO<Permission> getPermissionById(@PathVariable Long id) {
        return permissionService.getPermissionById(id);
    }

    @Operation(summary = "Créer une nouvelle permission")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Permission créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Erreur de validation des données")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDTO<Permission> createPermission(@Valid @RequestBody Permission permission, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseDTO<>(ResponseDTO.ERROR, "Erreurs de validation", null);
        }
        return permissionService.createPermission(permission);
    }

    @Operation(summary = "Mettre à jour une permission existante")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Permission mise à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Permission non trouvée"),
            @ApiResponse(responseCode = "400", description = "Erreur de validation des données")
    })
    @PutMapping("/{id}")
    public ResponseDTO<Permission> updatePermission(@PathVariable Long id, @Valid @RequestBody Permission permission, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseDTO<>(ResponseDTO.ERROR, "Erreurs de validation", null);
        }
        return permissionService.updatePermission(id, permission);
    }

    @Operation(summary = "Supprimer une permission")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Permission supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Permission non trouvée")
    })
    @DeleteMapping("/{id}")
    public ResponseDTO<String> deletePermission(@PathVariable Long id) {
        return permissionService.deletePermission(id);
    }
}
