package com.gi.billingservice.client;

public interface UserClient {
    UserDTO getUser(Long userId);
    
    class UserDTO {
        private Long id;
        private String firstName;
        private String lastName;
        private String login;
        private String phoneNumber;
        private String role;
        private Long clinicId;
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getLogin() { return login; }
        public void setLogin(String login) { this.login = login; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public Long getClinicId() { return clinicId; }
        public void setClinicId(Long clinicId) { this.clinicId = clinicId; }
        
        // Méthode helper pour obtenir le nom complet
        public String getFullName() {
            StringBuilder name = new StringBuilder();
            if (firstName != null && !firstName.isEmpty()) {
                name.append(firstName);
            }
            if (lastName != null && !lastName.isEmpty()) {
                if (name.length() > 0) name.append(" ");
                name.append(lastName);
            }
            return name.toString();
        }
    }
}
