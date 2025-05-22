const express = require('express');
const { 
    getDeliverers, 
    assignDeliverer, 
    getDelivererOrders, 
    updateDeliveryStatus, 
    upload, 
    uploadDeliveryProof 
} = require('../../controllers/deliveryController');

const router = express.Router();

// Admin routes
router.get('/deliverers', getDeliverers);
router.post('/orders/assign', assignDeliverer);

// Deliverer routes
router.get('/deliverers/:delivererId/orders', getDelivererOrders);
router.put('/orders/:orderId/status', updateDeliveryStatus);
router.post('/orders/:orderId/proof', upload.single('proof_image'), uploadDeliveryProof);

module.exports = router;