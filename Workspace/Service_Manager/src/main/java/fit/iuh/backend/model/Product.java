package fit.iuh.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Đổi từ productId thành id

    private String name;
    private String code; // Thêm code từ Service Catalog
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private Integer quantity; // Đổi từ stock thành quantity
    private Double price;
    private String status; // Thêm status từ Service Catalog và Cart Order
    private Integer gender;
    private String material;
    private Integer goldKarat;
    private String color;
    private String brand;
    private String size; // Thêm size từ Service Catalog
    private Integer viewCount;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "collection_id")
    private Collection collection;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductImage> imageSet;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "product")
    private List<Review> reviews;

    @OneToMany(mappedBy = "product")
    private List<OrderDetail> orderDetails;

    @OneToMany(mappedBy = "product")
    private List<CartItem> cartItems;
    
    @OneToMany(mappedBy = "product")
    private List<ProductFeature> productFeatures; // Thêm quan hệ productFeatures
    
    @OneToMany(mappedBy = "product")
    private List<ProductVariant> productVariants; // Thêm quan hệ productVariants
    
    @OneToMany(mappedBy = "product")
    private List<ProductSalesSummary> salesSummaries; // Thêm quan hệ salesSummaries
}