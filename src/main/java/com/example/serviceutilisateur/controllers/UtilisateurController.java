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


}
