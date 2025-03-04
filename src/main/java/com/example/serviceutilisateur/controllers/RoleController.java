package com.example.serviceutilisateur.controllers;

import com.example.serviceutilisateur.models.Role;
import com.example.serviceutilisateur.responses.ResponseDTO;
import com.example.serviceutilisateur.services.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Operation(summary = "Obtenir toutes les rôles")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des rôles récupérée avec succès"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping
    public ResponseDTO<List<Role>> getAllRoles() {
        return roleService.getAllRoles();
    }

    @Operation(summary = "Obtenir un rôle par ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rôle récupéré avec succès"),
            @ApiResponse(responseCode = "404", description = "Rôle non trouvé")
    })
    @GetMapping("/{id}")
    public ResponseDTO<Role> getRoleById(@PathVariable Long id) {
        return roleService.getRoleById(id);
    }

    @Operation(summary = "Créer un nouveau rôle")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Rôle créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Requête invalide")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDTO<Role> createRole(@RequestBody Role role) {
        return roleService.createRole(role);
    }

    @Operation(summary = "Mettre à jour un rôle existant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rôle mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Rôle non trouvé"),
            @ApiResponse(responseCode = "400", description = "Requête invalide")
    })
    @PutMapping("/{id}")
    public ResponseDTO<Role> updateRole(@PathVariable Long id, @RequestBody Role role) {
        return roleService.updateRole(id, role);
    }

    @Operation(summary = "Supprimer un rôle")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rôle supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Rôle non trouvé")
    })
    @DeleteMapping("/{id}")
    public ResponseDTO<String> deleteRole(@PathVariable Long id) {
        return roleService.deleteRole(id);
    }
}
