package fit.iuh.backend.controller.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequest {
    private String name;
    private String code; // Thêm code
    private String description;
    private Integer stock; // Giữ tên cũ để tương thích, service sẽ map sang quantity
    private Double price;
    private String status; // Thêm status
    private Integer gender;
    private String material;
    private Integer goldKarat;
    private String color;
    private String brand;
    private String size; // Thêm size
    private Integer viewCount;
    private Long categoryId;
    private Long collectionId;
}