const User = require('./User');
const Cart = require('./Cart');
const Product = require('./Product');
const CartItem = require('./CartItem');
const Order = require('./Order');
const OrderDetail = require('./OrderDetail');
const ProductImage = require('./ProductImage');
const DeliveryProof = require('./DeliveryProof');

// Định nghĩa lại các mối quan hệ sử dụng tên cột đã cập nhật
Cart.hasMany(CartItem, { foreignKey: 'cart_id', as: 'cartItems', onDelete: 'CASCADE' });
CartItem.belongsTo(Cart, { foreignKey: 'cart_id', as: 'cart' });

Product.hasMany(CartItem, { foreignKey: 'product_id', as: 'productItems', onDelete: 'CASCADE' });
CartItem.belongsTo(Product, { foreignKey: 'product_id', as: 'product' });

User.hasOne(Cart, { foreignKey: 'user_id', as: 'userCart', onDelete: 'CASCADE' });
Cart.belongsTo(User, { foreignKey: 'user_id', as: 'user' });

User.hasMany(Order, { foreignKey: 'user_id', as: 'orders', onDelete: 'CASCADE' });
Order.belongsTo(User, { foreignKey: 'user_id', as: 'user' });

Order.hasMany(OrderDetail, { foreignKey: 'order_id', as: 'orderDetails', onDelete: 'CASCADE' });
OrderDetail.belongsTo(Order, { foreignKey: 'order_id', as: 'order' });

Product.hasMany(OrderDetail, { foreignKey: 'product_id', as: 'productOrders', onDelete: 'CASCADE' });
OrderDetail.belongsTo(Product, { foreignKey: 'product_id', as: 'product' });

Product.hasMany(ProductImage, { foreignKey: 'product_id', as: 'imageSet' });
ProductImage.belongsTo(Product, { foreignKey: 'product_id', as: 'product' });

Order.belongsTo(User, { foreignKey: 'deliverer_id', as: 'deliverer' });
User.hasMany(Order, { foreignKey: 'deliverer_id', as: 'deliveries' });

Order.hasOne(DeliveryProof, { foreignKey: 'order_id', as: 'deliveryProof' });
DeliveryProof.belongsTo(Order, { foreignKey: 'order_id', as: 'order' });

DeliveryProof.belongsTo(User, { foreignKey: 'deliverer_id', as: 'deliverer' });
User.hasMany(DeliveryProof, { foreignKey: 'deliverer_id', as: 'deliveryProofs' });

module.exports = {
    User,
    Cart,
    Product,
    CartItem,
    Order,
    OrderDetail,
    ProductImage,
    DeliveryProof
};