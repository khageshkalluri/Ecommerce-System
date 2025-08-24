package com.ecommerce.authservice.Repository;

import com.ecommerce.authservice.Model.AuthUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<AuthUsers, String> {
    Optional<AuthUsers> findByEmail(String email);
}
