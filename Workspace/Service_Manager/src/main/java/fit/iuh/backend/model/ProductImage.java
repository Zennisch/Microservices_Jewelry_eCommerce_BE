package fit.iuh.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "product_images")
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Đổi từ imageId thành id

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    
    @Column(columnDefinition = "TEXT")
    private String imageUrl; // Đổi từ imageURL thành imageUrl
    
    private Boolean isPrimary; // Đổi từ isThumbnail thành isPrimary
    private Integer sortOrder; // Thêm sortOrder từ Service Cart Order
}