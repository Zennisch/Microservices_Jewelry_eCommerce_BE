const express = require('express');
const {
    createOrder,
    getOrders,
    getOrderById,
    deleteOrder,
    getOrderDetailById,
    getOrderByIdUser,
    getUserById,
    updateOrderPaymentStatus,
} = require('../../controllers/orderControllers');

const router = express.Router();

router.post('/orders', createOrder);
router.get('/orders', getOrders);
router.get('/orders/:orderId', getOrderById); // Đổi từ orderID
router.delete('/orders/:orderId', deleteOrder); // Đổi từ orderID
router.get('/orders/:orderId/details', getOrderDetailById); // Đổi từ orderID
router.get('/orders/user/:userId', getOrderByIdUser); // Đổi từ userID
router.get('/user/:userId', getUserById); // Đổi từ userID
router.put('/orders/:orderId/payment-status', updateOrderPaymentStatus);

module.exports = router;