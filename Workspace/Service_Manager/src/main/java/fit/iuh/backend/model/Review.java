package fit.iuh.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Đổi từ reviewId thành id

    @ManyToOne
    @JoinColumn(name = "user_id") // Giữ nguyên snake_case cho tên cột
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id") // Giữ nguyên snake_case cho tên cột
    private Product product;

    private String content;
    private Integer star;
}