package com.example.serviceutilisateur.implementations;

import com.example.serviceutilisateur.execptions.ResourceNotFoundException;
import com.example.serviceutilisateur.execptions.ValidationException;
import com.example.serviceutilisateur.models.Permission;
import com.example.serviceutilisateur.repositories.PermissionRepository;
import com.example.serviceutilisateur.responses.ResponseDTO;
import com.example.serviceutilisateur.services.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    private <T> ResponseDTO<T> handleDatabaseException(Exception e, String action, T data) {
        return new ResponseDTO<>(ResponseDTO.ERROR, "Erreur lors de " + action + ": " + e.getMessage(), data);
    }

    @Override
    public ResponseDTO<List<Permission>> getAllPermissions() {
        try {
            List<Permission> permissions = permissionRepository.findAll();
            if (permissions.isEmpty()) {
                return new ResponseDTO<>(ResponseDTO.NOT_FOUND, "Aucune permission trouvée", null);
            }
            return new ResponseDTO<>(ResponseDTO.SUCCESS, "Permissions récupérées avec succès", permissions);
        } catch (Exception e) {
            return handleDatabaseException(e, "récupération des permissions", null);
        }
    }

    @Override
    public ResponseDTO<Permission> getPermissionById(Long id) {
        try {
            Optional<Permission> permissionOptional = permissionRepository.findById(id);
            if (permissionOptional.isEmpty()) {
                throw new ResourceNotFoundException("Permission non trouvée avec l'id : " + id);
            }
            return new ResponseDTO<>(ResponseDTO.SUCCESS, "Permission récupérée avec succès", permissionOptional.get());
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            return handleDatabaseException(e, "récupération de la permission", null);
        }
    }

    @Override
    @Transactional
    public ResponseDTO<Permission> createPermission(Permission permission) {
        try {
            Permission savedPermission = permissionRepository.save(permission);
            return new ResponseDTO<>(ResponseDTO.CREATED, "Permission créée avec succès", savedPermission);
        } catch (DataIntegrityViolationException e) {
            throw new ValidationException("Violation d'intégrité des données lors de la création de la permission");
        } catch (Exception e) {
            return handleDatabaseException(e, "création de la permission", permission);
        }
    }

    @Override
    @Transactional
    public ResponseDTO<Permission> updatePermission(Long id, Permission permission) {
        try {
            Optional<Permission> existingPermissionOptional = permissionRepository.findById(id);
            if (existingPermissionOptional.isEmpty()) {
                throw new ResourceNotFoundException("Permission non trouvée avec l'id : " + id);
            }

            Permission existingPermission = existingPermissionOptional.get();
            existingPermission.setNom(permission.getNom());
            existingPermission.setDescription(permission.getDescription());
            existingPermission.setUpdatedAt(permission.getUpdatedAt());

            Permission updatedPermission = permissionRepository.save(existingPermission);

            return new ResponseDTO<>(ResponseDTO.SUCCESS, "Permission mise à jour avec succès", updatedPermission);
        } catch (DataIntegrityViolationException e) {
            throw new ValidationException("Violation d'intégrité des données lors de la mise à jour de la permission");
        } catch (Exception e) {
            return handleDatabaseException(e, "mise à jour de la permission", permission);
        }
    }

    @Override
    @Transactional
    public ResponseDTO<String> deletePermission(Long id) {
        try {
            Optional<Permission> permissionOptional = permissionRepository.findById(id);
            if (permissionOptional.isEmpty()) {
                throw new ResourceNotFoundException("Permission non trouvée avec l'id : " + id);
            }

            permissionRepository.deleteById(id);
            return new ResponseDTO<>(ResponseDTO.SUCCESS, "Permission supprimée avec succès", null);
        } catch (Exception e) {
            return handleDatabaseException(e, "suppression de la permission", null);
        }
    }
}
