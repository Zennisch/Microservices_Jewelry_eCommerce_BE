const {DataTypes} = require('sequelize');
const sequelize = require('../config/Database');

const DeliveryProof = sequelize.define(
    'DeliveryProof',
    {
        id: {
            type: DataTypes.INTEGER,
            primaryKey: true,
            autoIncrement: true,
        },
        order_id: {
            type: DataTypes.INTEGER,
            allowNull: false,
        },
        deliverer_id: {
            type: DataTypes.BIGINT,
            allowNull: false,
        },
        image_url: {
            type: DataTypes.STRING,
            allowNull: false,
        },
        notes: {
            type: DataTypes.TEXT,
            allowNull: true,
        },
        created_at: {
            type: DataTypes.DATE,
            defaultValue: DataTypes.NOW,
        },
        updated_at: {
            type: DataTypes.DATE,
            allowNull: true,
        },
    },
    {
        tableName: 'delivery_proofs',
        timestamps: true,
        underscored: true,
    }
);

module.exports = DeliveryProof;
