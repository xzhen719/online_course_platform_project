package com.ocp.backend.repository;

import com.ocp.backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    //Select * from categories where name = ?
    Optional<Category> findByName(String name);
}
