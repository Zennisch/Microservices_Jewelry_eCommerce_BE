package fit.iuh.backend.controller.response;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderPageResponse extends PageResponseAbstract implements Serializable {
    private List<OrderResponse> orders; // Đã sửa từ products thành orders
}