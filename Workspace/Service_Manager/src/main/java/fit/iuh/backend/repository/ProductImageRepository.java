package fit.iuh.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fit.iuh.backend.model.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProduct_Id(Long productId);
}