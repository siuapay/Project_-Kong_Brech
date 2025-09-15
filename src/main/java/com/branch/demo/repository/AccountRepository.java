package com.branch.demo.repository;

import com.branch.demo.domain.Account;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    Optional<Account> findByUsername(String username);
    
    Optional<Account> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    boolean existsByEmailAndIdNot(String email, Long id);
    
    @Query("SELECT a FROM Account a WHERE a.username = :username OR a.email = :username")
    Optional<Account> findByUsernameOrEmail(@Param("username") String username);
    
    @Query("SELECT COUNT(a) FROM Account a WHERE a.role = 'ADMIN' AND a.status = 'ACTIVE'")
    long countActiveAdmins();
    
    Page<Account> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrFullNameContainingIgnoreCase(
            String username, String email, String fullName, Pageable pageable);
}