package fit.iuh.backend.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageRequest {
    private String url;
    @JsonProperty("isPrimary")
    private boolean thumbnail; // Giữ tên biến nhưng đổi property name
    private Integer sortOrder; // Thêm sortOrder
}