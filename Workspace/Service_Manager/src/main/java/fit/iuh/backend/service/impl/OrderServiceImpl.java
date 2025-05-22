package fit.iuh.backend.service.impl;

import org.springdoc.api.OpenApiResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fit.iuh.backend.common.OrderStatus;
import fit.iuh.backend.controller.response.OrderPageResponse;
import fit.iuh.backend.controller.response.OrderResponse;
import fit.iuh.backend.model.Order;
import fit.iuh.backend.repository.OrderRepository;
import fit.iuh.backend.service.OrderService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j(topic = "ORDER-SERVICE")
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public OrderPageResponse getAllOrders(int page, int size) {
        log.info("Getting all orders for page {} with size {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Order> orders = orderRepository.findAll(pageable);
        
        List<OrderResponse> orderResponseList = orders.getContent().stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
        
        OrderPageResponse response = new OrderPageResponse();
        response.setPageNumber(page);
        response.setPageSize(size);
        response.setTotalElements(orders.getTotalElements());
        response.setTotalPages(orders.getTotalPages());
        response.setOrders(orderResponseList);
        
        return response;
    }

    @Override
    @Transactional
    public Long updateOrder(Long orderId, OrderStatus newStatus) {
        log.info("Updating order {} with status {}", orderId, newStatus);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OpenApiResourceNotFoundException("Order not found with id: " + orderId));
        
        order.setStatus(newStatus);
        orderRepository.save(order);
        
        return orderId;
    }
    
    // Helper method to map Order entity to OrderResponse
    private OrderResponse mapToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getId());
        
        if (order.getUser() != null) {
            response.setUserName(order.getUser().getName());
            response.setUserEmail(order.getUser().getEmail());
        }
        
        if (order.getDeliverer() != null) {
            response.setDelivererName(order.getDeliverer().getName());
            response.setDelivererEmail(order.getDeliverer().getEmail());
        }
        
        response.setOrderStatus(order.getStatus());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        response.setAddress(order.getAddress());
        response.setStatus(order.getStatus());
        response.setPaymentStatus(order.getPaymentStatus());
        response.setPaymentMethod(order.getPaymentMethod());
        response.setHasDeliveryProof(order.getDeliveryProof() != null);
        
        return response;
    }
}