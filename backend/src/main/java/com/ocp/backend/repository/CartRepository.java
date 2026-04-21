package com.ocp.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ocp.backend.entity.Cart;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    //Returning Optional object compel null handling, and to avoid nullPointerException.
    Optional<Cart> findByUserId(Long userId);
}
