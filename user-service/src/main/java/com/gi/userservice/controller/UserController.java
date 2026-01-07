package com.gi.userservice.controller;

import com.gi.userservice.model.dto.UserDTO;
import com.gi.userservice.model.dto.request.CreateUserRequest;
import com.gi.userservice.model.enums.UserRole;
import com.gi.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserDTO> createUser(@RequestBody CreateUserRequest request) {
        UserDTO dto = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        UserDTO dto = userService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> dtos = userService.findAll();
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO dto) {
        UserDTO updated = userService.updateUser(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<UserDTO> activateUser(@PathVariable Long id) {
        UserDTO dto = userService.activateUser(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<UserDTO> deactivateUser(@PathVariable Long id) {
        UserDTO dto = userService.deactivateUser(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/clinic/{clinicId}")
    public ResponseEntity<List<UserDTO>> listByClinic(@PathVariable Long clinicId) {
        List<UserDTO> dtos = userService.listByClinic(clinicId);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserDTO>> listByRole(@PathVariable UserRole role) {
        List<UserDTO> dtos = userService.listByRole(role);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/login/{login}")
    public ResponseEntity<UserDTO> getUserByLogin(@PathVariable String login) {
        System.out.println("getUserByLogin" + login);
        UserDTO dto = userService.findByLogin(login);
        System.out.println(dto);
        return ResponseEntity.ok(dto);
    }
}