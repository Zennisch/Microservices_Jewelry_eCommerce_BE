package Service_Catalog.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "products")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "gender")
    private Integer gender;

    @Column(name = "material")
    private String material;

    @Column(name = "gold_karat")
    private Integer goldKarat;

    @Column(name = "color", length = 50)
    private String color;

    @Column(name = "brand")
    private String brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties("products")
    private Category category;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "product")
    @JsonIgnoreProperties("product")
    private List<ProductImage> productImages = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "collection_id")
    @JsonIgnoreProperties({"products"})
    private Collection collection;

    @Column(name = "size")
    private String size;

    @Column(name = "view_count")
    private Integer viewCount;

    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<ProductSalesSummary> productSalesSummaries = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<ProductFeature> productFeatures = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<ProductVariant> productVariants = new ArrayList<>();

    // Phương thức tương thích ngược
    @Deprecated
    public Integer getStock() {
        return this.quantity;
    }

    @Deprecated
    public void setStock(Integer stock) {
        this.quantity = stock;
    }

    @Deprecated
    public Category getCategoryId() {
        return this.category;
    }

    @Deprecated
    public void setCategoryId(Category category) {
        this.category = category;
    }

    @Deprecated
    public Collection getCollectionId() {
        return this.collection;
    }

    @Deprecated
    public void setCollectionId(Collection collection) {
        this.collection = collection;
    }
}