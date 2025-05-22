package fit.iuh.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fit.iuh.backend.common.OrderStatus;
import fit.iuh.backend.model.Order;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    List<Order> findByUser_Id(Long userId);
    List<Order> findByDeliverer_Id(Long delivererId);
}