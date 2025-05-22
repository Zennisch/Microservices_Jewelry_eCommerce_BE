package fit.iuh.backend.controller.response;

import java.time.LocalDateTime;

import fit.iuh.backend.common.OrderStatus;
import fit.iuh.backend.common.PaymentMethod;
import fit.iuh.backend.common.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long orderId; // Giữ tên cũ để tương thích với frontend
    private String userName;
    private String userEmail;
    private String delivererName; // Thêm thông tin người giao hàng
    private String delivererEmail; // Thêm thông tin người giao hàng
    private OrderStatus orderStatus; // Trùng lặp với status, nên bỏ một trong hai
    private LocalDateTime createdAt; // Đổi createAt thành createdAt
    private LocalDateTime updatedAt; // Thêm updatedAt
    private String address;
    private OrderStatus status;
    private PaymentStatus paymentStatus; // Thêm paymentStatus
    private PaymentMethod paymentMethod; // Thêm paymentMethod
    private boolean hasDeliveryProof; // Thêm thông tin về proof
}