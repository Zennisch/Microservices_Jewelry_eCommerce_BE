package fit.iuh.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Đổi từ categoryId thành id

    private String name;
    private String description;

    @OneToMany(mappedBy = "category")
    private List<Product> products;
}