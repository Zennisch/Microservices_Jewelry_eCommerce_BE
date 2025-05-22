package fit.iuh.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import fit.iuh.backend.common.OrderStatus;
import fit.iuh.backend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(
            summary = "Get all orders",
            description = "Retrieves a paginated list of all orders"
    )
    @GetMapping
    public Map<String, Object> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "All orders retrieved successfully");
        result.put("data", orderService.getAllOrders(page, size));
        
        return result;
    }

    @Operation(
            summary = "Update order status",
            description = "Updates the status of an existing order"
    )
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Object> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus newStatus) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Order status updated successfully");
        result.put("data", orderService.updateOrder(orderId, newStatus));
        
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}