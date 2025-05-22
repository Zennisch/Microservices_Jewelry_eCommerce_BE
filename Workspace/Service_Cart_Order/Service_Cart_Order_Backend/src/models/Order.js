const { DataTypes } = require('sequelize');
const sequelize = require('../config/Database');
const OrderStatus = require('./OrderStatus');

const Order = sequelize.define('Order', {
    id: { // Đổi từ orderID thành id
        type: DataTypes.INTEGER,
        primaryKey: true,
        autoIncrement: true,
    },
    user_id: { // Đổi từ userID thành user_id
        type: DataTypes.BIGINT, // Đổi sang BIGINT để phù hợp với id user
        allowNull: false,
    },
    deliverer_id: {
        type: DataTypes.BIGINT,
        allowNull: true,
    },
    created_at: { // Đổi từ createAt thành created_at
        type: DataTypes.DATE,
        defaultValue: DataTypes.NOW,
    },
    updated_at: { // Thêm updated_at
        type: DataTypes.DATE,
        allowNull: true,
    },
    address: {
        type: DataTypes.STRING,
        allowNull: false,
    },
    status: {
        type: DataTypes.STRING,
        defaultValue: OrderStatus.PENDING,
        allowNull: false,
    },
    payment_status: { // Đổi từ paymentStatus thành payment_status
        type: DataTypes.STRING,
        defaultValue: 'PENDING',
    },
    payment_method: { 
        type: DataTypes.STRING,
        defaultValue: 'COD',
    },
}, {
    tableName: 'orders',
    timestamps: true,
    underscored: true,
});

module.exports = Order;