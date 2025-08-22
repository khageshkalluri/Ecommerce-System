package com.ecommerce.usersservice.Repository;

import com.ecommerce.usersservice.Dto.UsersDto;
import com.ecommerce.usersservice.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface UserRepository extends JpaRepository<Users, UUID> {
    Boolean existsByEmail(String email);
    Users findByEmail(String email);
    List<Users> findByName(String name);
}
