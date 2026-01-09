package com.gi.userservice.repository;

import com.gi.userservice.model.entity.User;
import com.gi.userservice.model.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLogin(String login);

    List<User> findByRole(UserRole role);

    List<User> findByClinicIdAndRole(Long clinicId, UserRole role);
}