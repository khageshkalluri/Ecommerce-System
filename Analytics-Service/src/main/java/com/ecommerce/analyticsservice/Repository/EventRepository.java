package com.ecommerce.analyticsservice.Repository;

import com.ecommerce.analyticsservice.Model.UserEvents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<UserEvents, UUID> {
}
