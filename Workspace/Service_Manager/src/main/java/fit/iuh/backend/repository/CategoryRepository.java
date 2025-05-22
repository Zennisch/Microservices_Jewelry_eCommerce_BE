package fit.iuh.backend.repository;

import fit.iuh.backend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);

    // Change this method
    // Optional<Category> findByCategoryId(Long categoryId);
    
    // To this (to match the entity's field name)
    Optional<Category> findById(Long id);
}