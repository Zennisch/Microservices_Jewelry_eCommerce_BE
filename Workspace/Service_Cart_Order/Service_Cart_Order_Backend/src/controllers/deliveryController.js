const {User, Order, OrderDetail, DeliveryProof, Product, ProductImage} = require('../models');
const fs = require('fs');
const path = require('path');
const multer = require('multer');

// Get all deliverers
const getDeliverers = async (req, res) => {
    try {
        const deliverers = await User.findAll({
            where: {role_id: 2},
        });
        return res.status(200).json(deliverers);
    } catch (error) {
        console.error(error);
        return res.status(500).json({message: 'Lỗi khi lấy danh sách người giao hàng'});
    }
};

// Assign deliverer to an order
const assignDeliverer = async (req, res) => {
    try {
        const {orderId, delivererId} = req.body;

        const order = await Order.findByPk(orderId);
        if (!order) {
            return res.status(404).json({message: 'Không tìm thấy đơn hàng'});
        }

        const deliverer = await User.findOne({
            where: {
                id: delivererId,
                role_id: 2, // Ensure user is a deliverer
            },
        });
        if (!deliverer) {
            return res.status(404).json({message: 'Người giao hàng không tồn tại'});
        }

        await order.update({
            deliverer_id: delivererId,
            status: 'ASSIGNED_TO_DELIVERER',
        });

        return res.status(200).json({
            message: 'Đã gán người giao hàng thành công',
            order,
        });
    } catch (error) {
        console.error(error);
        return res.status(500).json({message: 'Lỗi khi gán người giao hàng'});
    }
};

// Configure multer for file upload
const storage = multer.diskStorage({
    destination: (req, file, cb) => {
        const uploadDir = path.join(__dirname, '../public/uploads/delivery-proofs');
        if (!fs.existsSync(uploadDir)) {
            fs.mkdirSync(uploadDir, {recursive: true});
        }
        cb(null, uploadDir);
    },
    filename: (req, file, cb) => {
        cb(null, `${Date.now()}-${file.originalname}`);
    },
});

const upload = multer({
    storage,
    limits: {fileSize: 5 * 1024 * 1024}, // 5MB limit
    fileFilter: (req, file, cb) => {
        if (!file.mimetype.startsWith('image/')) {
            return cb(new Error('Only images are allowed'));
        }
        cb(null, true);
    },
});

// Upload delivery proof
const uploadDeliveryProof = async (req, res) => {
    try {
        const {orderId} = req.params;
        // Lấy delivererId từ body nếu req.user không tồn tại
        const {notes, delivererId: bodyDelivererId} = req.body;
        const delivererId = req.user?.id || bodyDelivererId;
        
        if (!delivererId) {
            return res.status(400).json({
                message: 'Thiếu delivererId. Vui lòng đăng nhập hoặc cung cấp delivererId trong body request.'
            });
        }

        if (!req.file) {
            return res.status(400).json({message: 'Vui lòng tải lên hình ảnh'});
        }

        const order = await Order.findOne({
            where: {
                id: orderId,
                deliverer_id: delivererId,
            },
        });

        if (!order) {
            return res.status(404).json({message: 'Không tìm thấy đơn hàng hoặc bạn không được phân công giao đơn hàng này'});
        }
        
        // Kiểm tra xem trạng thái đơn hàng có phải là DELIVERED
        if (order.status !== 'DELIVERED') {
            return res.status(400).json({
                message: 'Đơn hàng phải ở trạng thái DELIVERED trước khi tải lên bằng chứng'
            });
        }

        // Save image URL
        const imageUrl = `/uploads/delivery-proofs/${req.file.filename}`;

        // Create delivery proof record
        const deliveryProof = await DeliveryProof.create({
            order_id: orderId,
            deliverer_id: delivererId,
            image_url: imageUrl,
            notes,
        });

        // Update order status
        await order.update({
            status: 'DELIVERY_CONFIRMED',
        });

        return res.status(200).json({
            message: 'Tải lên bằng chứng giao hàng thành công',
            deliveryProof,
            order,
        });
    } catch (error) {
        console.error(error);
        return res.status(500).json({message: 'Lỗi khi tải lên bằng chứng giao hàng'});
    }
};

// Other methods for deliverers to manage their orders
const getDelivererOrders = async (req, res) => {
    try {
        const {delivererId} = req.params;

        const orders = await Order.findAll({
            where: {deliverer_id: delivererId},
            include: [
                {model: User, as: 'user'},
                {model: OrderDetail, as: 'orderDetails'},
                {
                    model: OrderDetail,
                    as: 'orderDetails',
                    include: [
                        {
                            model: Product,
                            as: 'product',
                            include: [{model: ProductImage, as: 'imageSet', limit: 1}],
                        },
                    ],
                },
            ],
        });

        return res.status(200).json(orders);
    } catch (error) {
        console.error(error);
        return res.status(500).json({message: 'Lỗi khi lấy danh sách đơn hàng của người giao hàng'});
    }
};

// Update order status by deliverer
const updateDeliveryStatus = async (req, res) => {
    try {
        const {orderId} = req.params;
        const {status, delivererId} = req.body; // Lấy delivererId từ body request
        
        // Kiểm tra nếu req.user tồn tại, lấy id từ đó, ngược lại dùng delivererId từ body
        const actualDelivererId = req.user?.id || delivererId;
        
        if (!actualDelivererId) {
            return res.status(400).json({
                message: 'Thiếu delivererId. Vui lòng đăng nhập hoặc cung cấp delivererId trong body request.'
            });
        }

        const order = await Order.findOne({
            where: {
                id: orderId,
                deliverer_id: actualDelivererId,
            },
        });

        if (!order) {
            return res.status(404).json({message: 'Không tìm thấy đơn hàng hoặc bạn không được phân công giao đơn hàng này'});
        }

        // Kiểm tra trạng thái hiện tại trước khi chuyển sang trạng thái mới
        const validTransitions = {
            "ASSIGNED_TO_DELIVERER": ["OUT_FOR_DELIVERY", "FAILED"],
            "OUT_FOR_DELIVERY": ["DELIVERED", "FAILED"],
            "DELIVERED": ["FAILED"]
        };

        if (!validTransitions[order.status] || !validTransitions[order.status].includes(status)) {
            return res.status(400).json({
                message: `Không thể chuyển từ trạng thái ${order.status} sang ${status}`
            });
        }

        // Các trạng thái hợp lệ người giao hàng có thể đặt
        const allowedStatus = ['OUT_FOR_DELIVERY', 'DELIVERED', 'FAILED'];
        if (!allowedStatus.includes(status)) {
            return res.status(400).json({message: 'Trạng thái không hợp lệ'});
        }

        await order.update({status});

        return res.status(200).json({
            message: 'Cập nhật trạng thái đơn hàng thành công',
            order,
        });
    } catch (error) {
        console.error(error);
        return res.status(500).json({message: 'Lỗi khi cập nhật trạng thái đơn hàng'});
    }
};

module.exports = {
    getDeliverers,
    assignDeliverer,
    getDelivererOrders,
    updateDeliveryStatus,
    upload,
    uploadDeliveryProof,
};
