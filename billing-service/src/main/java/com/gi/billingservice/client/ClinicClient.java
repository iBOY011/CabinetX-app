package com.gi.billingservice.client;

public interface ClinicClient {
    ClinicDTO getClinic(Long clinicId);
    
    class ClinicDTO {
        private Long id;
        private String name;
        private String specialty;
        private String phone;
        private String address;
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getSpecialty() { return specialty; }
        public void setSpecialty(String specialty) { this.specialty = specialty; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
    }
}
