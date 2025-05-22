package fit.iuh.backend.common;

public enum OrderStatus {
    PENDING,
    PROCESSING,
    READY_FOR_DELIVERY,
    ASSIGNED_TO_DELIVERER,
    OUT_FOR_DELIVERY,
    DELIVERED,
    DELIVERY_CONFIRMED,
    SUCCESS,
    FAILED,
    CANCELED
}