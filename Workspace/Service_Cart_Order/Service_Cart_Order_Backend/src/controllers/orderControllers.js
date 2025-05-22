const {Order, OrderDetail, Product, User, DeliveryProof} = require('../models');
const ProductImage = require('../models/ProductImage');

const createOrder = async (req, res) => {
    try {
        const {userId, address, status, orderDetails, paymentStatus, paymentMethod} = req.body;

        const user = await User.findByPk(userId);
        if (!user) {
            return res.status(404).json({message: 'Người dùng không tồn tại'});
        }

        const order = await Order.create({
            user_id: userId,
            address,
            status,
            payment_status: paymentStatus,
            payment_method: paymentMethod, // Lưu phương thức thanh toán
        });

        // Tạo chi tiết đơn hàng
        const orderItems = orderDetails.map((detail) => ({
            order_id: order.id,
            product_id: detail.productId,
            quantity: detail.quantity,
            price: detail.price,
        }));
        await OrderDetail.bulkCreate(orderItems);

        return res.status(201).json({message: 'Đơn hàng được tạo thành công', order});
    } catch (error) {
        console.error(error);
        return res.status(500).json({message: 'Lỗi khi tạo đơn hàng'});
    }
};

const getOrders = async (req, res) => {
    try {
        const orders = await Order.findAll({
            include: [{model: User, as: 'user'}],
        });
        return res.status(200).json(orders);
    } catch (error) {
        console.error(error);
        return res.status(500).json({message: 'Lỗi khi lấy danh sách đơn hàng'});
    }
};

const getOrderById = async (req, res) => {
    try {
        const {orderId} = req.params;
        const order = await Order.findByPk(orderId, {
            include: [
                {model: User, as: 'user'},
                {model: User, as: 'deliverer'},
                {model: DeliveryProof, as: 'deliveryProof'},
                {
                    model: OrderDetail,
                    as: 'orderDetails',
                    include: [
                        {
                            model: Product,
                            as: 'product',
                            include: [
                                {
                                    model: ProductImage,
                                    as: 'imageSet',
                                    attributes: ['image_url'],
                                    limit: 1,
                                },
                            ],
                        },
                    ],
                },
            ],
        });

        if (!order) {
            return res.status(404).json({message: 'Không tìm thấy đơn hàng'});
        }
        return res.status(200).json(order);
    } catch (error) {
        console.error(error);
        return res.status(500).json({message: 'Lỗi khi lấy đơn hàng'});
    }
};

const updateOrder = async (req, res) => {
    try {
        const {orderId} = req.params; // Đổi từ orderID
        const {address, status} = req.body;

        const order = await Order.findByPk(orderId);
        if (!order) {
            return res.status(404).json({message: 'Không tìm thấy đơn hàng'});
        }

        await order.update({address, status});
        return res.status(200).json({message: 'Cập nhật đơn hàng thành công', order});
    } catch (error) {
        console.error(error);
        return res.status(500).json({message: 'Lỗi khi cập nhật đơn hàng'});
    }
};

const deleteOrder = async (req, res) => {
    try {
        const {orderId} = req.params; // Đổi từ orderID

        const order = await Order.findByPk(orderId);
        if (!order) {
            return res.status(404).json({message: 'Không tìm thấy đơn hàng'});
        }

        await order.destroy();
        return res.status(200).json({message: 'Xóa đơn hàng thành công'});
    } catch (error) {
        console.error(error);
        return res.status(500).json({message: 'Lỗi khi xóa đơn hàng'});
    }
};

const getOrderDetailById = async (req, res) => {
    try {
        const {orderId} = req.params; // Đổi từ orderID

        const orderDetails = await OrderDetail.findAll({
            where: {order_id: orderId},
            include: [{model: Product, as: 'product'}],
        });

        if (orderDetails.length === 0) {
            return res.status(404).json({message: 'Không tìm thấy chi tiết đơn hàng'});
        }

        return res.status(200).json(orderDetails);
    } catch (error) {
        console.error(error);
        return res.status(500).json({message: 'Lỗi khi lấy chi tiết đơn hàng'});
    }
};

const getOrderByIdUser = async (req, res) => {
    try {
        const {userId} = req.params; // Đổi từ userID

        // Kiểm tra xem user có tồn tại không
        const user = await User.findByPk(userId);
        if (!user) {
            return res.status(404).json({message: 'Người dùng không tồn tại'});
        }

        // Lấy danh sách đơn hàng của user
        const orders = await Order.findAll({
            where: {user_id: userId},
            include: [
                {
                    model: OrderDetail,
                    as: 'orderDetails',
                    include: [
                        {
                            model: Product,
                            as: 'product',
                            include: [
                                {
                                    model: ProductImage,
                                    as: 'imageSet',
                                    attributes: ['image_url'],
                                    limit: 1,
                                },
                            ],
                        },
                    ],
                },
                { model: User, as: "deliverer" }, // Add this line
                { model: DeliveryProof, as: "deliveryProof" } // Add this line
            ],
        });

        if (orders.length === 0) {
            return res.status(404).json({message: 'Không tìm thấy đơn hàng nào'});
        }

        return res.status(200).json(orders);
    } catch (error) {
        console.error(error);
        return res.status(500).json({message: 'Lỗi khi lấy danh sách đơn hàng của người dùng'});
    }
};

//Tạm thời lấy user
const getUserById = async (userId) => {
    try {
        const user = await User.findOne({where: {id: userId}}); // Đổi từ userID

        if (!user) {
            return {error: 'Người dùng không tồn tại'};
        }

        return user;
    } catch (error) {
        console.error('Lỗi khi lấy thông tin người dùng:', error);
        return {error: 'Lỗi hệ thống'};
    }
};

const updateOrderPaymentStatus = async (req, res) => {
  try {
    const { orderId } = req.params;
    const { paymentStatus, transactionId } = req.body;

    const order = await Order.findByPk(orderId);
    if (!order) {
      return res.status(404).json({ message: 'Không tìm thấy đơn hàng' });
    }

    await order.update({ 
      payment_status: paymentStatus,
      transaction_id: transactionId
    });
    
    return res.status(200).json({ 
      message: 'Cập nhật trạng thái thanh toán thành công', 
      order 
    });
  } catch (error) {
    console.error(error);
    return res.status(500).json({ message: 'Lỗi khi cập nhật trạng thái thanh toán' });
  }
};

module.exports = {
    createOrder,
    getOrders,
    getOrderById,
    updateOrder,
    deleteOrder,
    getOrderDetailById,
    getOrderByIdUser,
    getUserById,
    updateOrderPaymentStatus
};
