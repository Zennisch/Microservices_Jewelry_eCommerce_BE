package fit.iuh.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "cart_items")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Đổi từ cartItemId thành id

    @ManyToOne
    @JoinColumn(name = "cart_id") // Giữ nguyên snake_case cho tên cột
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id") // Giữ nguyên snake_case cho tên cột
    private Product product;

    private Integer quantity;
    private Double price;
}