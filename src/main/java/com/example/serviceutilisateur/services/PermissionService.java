package com.example.serviceutilisateur.services;

import com.example.serviceutilisateur.models.Permission;
import com.example.serviceutilisateur.responses.ResponseDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface PermissionService {
    ResponseDTO<List<Permission>> getAllPermissions();
    ResponseDTO<Permission> getPermissionById(Long id);
    ResponseDTO<Permission> createPermission(@Valid Permission permission);
    ResponseDTO<Permission> updatePermission(Long id, @Valid Permission permission);
    ResponseDTO<String> deletePermission(Long id);
}
