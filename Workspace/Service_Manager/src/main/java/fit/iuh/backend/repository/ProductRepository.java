package fit.iuh.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fit.iuh.backend.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{
    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN p.category c " +
            "LEFT JOIN p.collection col " +
            "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR CAST(p.price AS string) LIKE CONCAT('%', :keyword, '%') " +
            "OR CAST(p.gender AS string) LIKE CONCAT('%', :keyword, '%') " +
            "OR LOWER(p.material) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR CAST(p.goldKarat AS string) LIKE CONCAT('%', :keyword, '%') " +
            "OR LOWER(p.color) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(col.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Product> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    // Changed field names to match entity properties
    Page<Product> findByQuantityLessThanEqual(int threshold, Pageable pageable);
    Page<Product> findByQuantityEquals(int quantity, Pageable pageable);
    
    Page<Product> findByStatus(String status, Pageable pageable);
    Page<Product> findByGender(Integer gender, Pageable pageable);
    
    // Fix field name to match Category entity
    Page<Product> findByCategory_Id(Long categoryId, Pageable pageable);
    
    // Keep CollectionId since it matches the entity
    Page<Product> findByCollection_CollectionId(Long collectionId, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.code = :code")
    Product findByCode(@Param("code") String code);
    
    @Query("SELECT p FROM Product p JOIN p.salesSummaries s GROUP BY p ORDER BY SUM(s.quantitySold) DESC")
    Page<Product> findTopSellingProducts(Pageable pageable);
}