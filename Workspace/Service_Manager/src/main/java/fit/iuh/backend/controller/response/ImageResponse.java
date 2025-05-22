package fit.iuh.backend.controller.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponse {
    private Long id;
    private String imageUrl; // Đổi từ imageURL thành imageUrl
    private Boolean isPrimary; // Đổi từ isThumbnail thành isPrimary
    private Integer sortOrder; // Thêm sortOrder
}