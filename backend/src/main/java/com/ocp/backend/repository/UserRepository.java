package com.ocp.backend.repository;

import com.ocp.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository //DAO interface, 提供service層方法與DB互動
public interface UserRepository extends JpaRepository<User, Long> {


    //Select * from users where email = ?
    Optional<User> findByEmail(String email);

    Optional<User> findByResetToken(String resetToken);

    boolean existsByEmail(String email);
}
