package fit.iuh.backend.model;

import fit.iuh.backend.common.OrderStatus;
import fit.iuh.backend.common.PaymentMethod;
import fit.iuh.backend.common.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Đổi từ orderId thành id

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "deliverer_id")
    private User deliverer; // Thêm trường deliverer
    
    private LocalDateTime createdAt; // Đổi từ createAt
    private LocalDateTime updatedAt; // Thêm updatedAt
    
    private String address;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus; // Thêm paymentStatus
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod; // Thêm paymentMethod

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails;
    
    @OneToOne(mappedBy = "order")
    private DeliveryProof deliveryProof; // Thêm quan hệ deliveryProof
}