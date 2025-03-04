package com.example.serviceutilisateur.services;

import com.example.serviceutilisateur.models.Role;
import com.example.serviceutilisateur.responses.ResponseDTO;

import java.util.List;

public interface RoleService {
    ResponseDTO<List<Role>> getAllRoles();
    ResponseDTO<Role> getRoleById(Long id);
    ResponseDTO<Role> createRole(Role role);
    ResponseDTO<Role> updateRole(Long id, Role role);
    ResponseDTO<String> deleteRole(Long id);
}
