package com.example.serviceutilisateur.implementations;

import com.example.serviceutilisateur.execptions.ResourceNotFoundException;
import com.example.serviceutilisateur.execptions.ValidationException;
import com.example.serviceutilisateur.models.Role;
import com.example.serviceutilisateur.repositories.RoleRepository;
import com.example.serviceutilisateur.responses.ResponseDTO;
import com.example.serviceutilisateur.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    private <T> ResponseDTO<T> handleDatabaseException(Exception e, String action, T data) {
        return new ResponseDTO<>(ResponseDTO.ERROR, "Erreur lors de " + action + ": " + e.getMessage(), data);
    }

    private <T> Optional<T> findByIdOrThrowException(Optional<T> optional, String entityName, Long id) {
        if (optional.isEmpty()) {
            throw new ResourceNotFoundException(entityName + " non trouvé avec l'id : " + id);
        }
        return optional;
    }

    @Override
    public ResponseDTO<List<Role>> getAllRoles() {
        try {
            List<Role> roles = roleRepository.findAll();
            if (roles.isEmpty()) {
                return new ResponseDTO<>(ResponseDTO.NOT_FOUND, "Aucun rôle trouvé", null);
            }
            return new ResponseDTO<>(ResponseDTO.SUCCESS, "Rôles récupérés avec succès", roles);
        } catch (Exception e) {
            return handleDatabaseException(e, "récupération des rôles", null);
        }
    }

    @Override
    public ResponseDTO<Role> getRoleById(Long id) {
        try {
            Optional<Role> roleOptional = roleRepository.findById(id);
            return new ResponseDTO<>(ResponseDTO.SUCCESS, "Rôle récupéré avec succès", findByIdOrThrowException(roleOptional, "Rôle", id).get());
        } catch (Exception e) {
            return handleDatabaseException(e, "récupération du rôle", null);
        }
    }

    @Override
    @Transactional
    public ResponseDTO<Role> createRole(Role role) {
        try {
            Role savedRole = roleRepository.save(role);
            return new ResponseDTO<>(ResponseDTO.CREATED, "Rôle créé avec succès", savedRole);
        } catch (DataIntegrityViolationException e) {
            throw new ValidationException("Violation d'intégrité des données lors de la création du rôle");
        } catch (Exception e) {
            return handleDatabaseException(e, "création du rôle", role);
        }
    }

    @Override
    @Transactional
    public ResponseDTO<Role> updateRole(Long id, Role role) {
        try {
            Optional<Role> existingRoleOptional = roleRepository.findById(id);
            Role existingRole = findByIdOrThrowException(existingRoleOptional, "Rôle", id).get();
            existingRole.setNom(role.getNom());
            existingRole.setDescription(role.getDescription());
            existingRole.setUpdatedAt(role.getUpdatedAt());

            Role updatedRole = roleRepository.save(existingRole);
            return new ResponseDTO<>(ResponseDTO.SUCCESS, "Rôle mis à jour avec succès", updatedRole);
        } catch (Exception e) {
            return handleDatabaseException(e, "mise à jour du rôle", role);
        }
    }

    @Override
    @Transactional
    public ResponseDTO<String> deleteRole(Long id) {
        try {
            Optional<Role> roleOptional = roleRepository.findById(id);
            findByIdOrThrowException(roleOptional, "Rôle", id);
            roleRepository.deleteById(id);
            return new ResponseDTO<>(ResponseDTO.SUCCESS, "Rôle supprimé avec succès", null);
        } catch (Exception e) {
            return handleDatabaseException(e, "suppression du rôle", null);
        }
    }
}
