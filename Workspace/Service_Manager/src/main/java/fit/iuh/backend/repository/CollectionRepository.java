package fit.iuh.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fit.iuh.backend.model.Collection;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long> {
    // This is redundant as JpaRepository already provides findById
    // Optional<Collection> findById(Long id);
    
    // Add a useful method
    Optional<Collection> findByName(String name);
}