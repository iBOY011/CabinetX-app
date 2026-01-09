package com.gi.consultationservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@FeignClient(name = "user-service")
public interface UserClient {
    
    @GetMapping("/api/users/by-cabinet-and-role")
    List<UserDTO> getUsersByCabinetAndRole(
            @RequestParam("cabinetId") Long cabinetId,
            @RequestParam("role") String role
    );
    
    class UserDTO {
        private Long id;
        private String email;
        private String role;
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}
