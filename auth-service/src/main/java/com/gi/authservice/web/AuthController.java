package com.gi.authservice.web;

import com.gi.authservice.dto.ChangePasswordRequest;
import com.gi.authservice.dto.LoginRequest;
import com.gi.authservice.dto.LoginResponse;
import com.gi.authservice.dto.RefreshTokenRequest;
import com.gi.authservice.dto.RegisterRequest;
import com.gi.authservice.entities.Compte;
import com.gi.authservice.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Compte register(@RequestBody @Valid RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest request, HttpServletRequest httpServletRequest) {
        String adresseIp = httpServletRequest.getRemoteAddr();
        return authService.login(request, adresseIp);
    }

    @PostMapping("/refresh")
    public LoginResponse refresh(@RequestBody @Valid RefreshTokenRequest request) {
        return authService.refreshToken(request);
    }

    @PostMapping("/logout/{utilisateurId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@PathVariable Long utilisateurId) {
        authService.logout(utilisateurId);
    }

    @PostMapping("/password/{utilisateurId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@PathVariable Long utilisateurId,
                               @RequestBody @Valid ChangePasswordRequest request) {
        authService.changePassword(utilisateurId, request);
    }

    @GetMapping("/access")
    public ResponseEntity<Map<String, Boolean>> verifierAcces(@RequestParam Long utilisateurId,
                                                              @RequestParam String permission) {
        boolean autorise = authService.verifierAcces(utilisateurId, permission);
        return ResponseEntity.ok(Map.of("autorise", autorise));
    }
}
