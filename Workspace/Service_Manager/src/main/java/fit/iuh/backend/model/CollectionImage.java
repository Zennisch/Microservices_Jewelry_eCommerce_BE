package fit.iuh.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "collection_images")
public class CollectionImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Đổi từ imageId thành id

    @ManyToOne
    @JoinColumn(name = "collection_id")
    private Collection collection;

    @Column(columnDefinition = "TEXT")
    private String imageUrl; // Đổi từ imageURL thành imageUrl
    
    private Boolean isPrimary; // Đổi từ isThumbnail thành isPrimary
    private Integer sortOrder; // Thêm sortOrder từ Service Catalog
}