package fit.iuh.backend.model;

import fit.iuh.backend.common.UserRole;
import fit.iuh.backend.common.UserState;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Đổi từ userId thành id

    @Column(unique = true)
    private String email; // Thêm email thay vì username

    private String password; // Đổi từ passwordHash
    private String name;
    private Integer age; // Thêm age từ Service Cart Order
    private String gender; // Thêm gender từ Service Cart Order
    private String address; // Đổi từ addresses
    private String avatar; // Đổi từ profileImageURL

    @Column(columnDefinition = "TEXT")
    private String refreshToken; // Thêm refresh_token
    private String resetToken; // Thêm reset_token
    
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy; // Thêm created_by
    private String updatedBy; // Thêm updated_by

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    private UserState state;

    @OneToMany(mappedBy = "user")
    private List<Order> orders;

    @OneToMany(mappedBy = "user")
    private List<Review> reviews;

    @OneToOne(mappedBy = "user")
    private Cart cart;

    @OneToMany(mappedBy = "user")
    private List<UserActivity> activities;
    
    @OneToMany(mappedBy = "deliverer")
    private List<Order> deliveries; // Thêm quan hệ deliveries
    
    @OneToMany(mappedBy = "deliverer")
    private List<DeliveryProof> deliveryProofs; // Thêm quan hệ deliveryProofs
}