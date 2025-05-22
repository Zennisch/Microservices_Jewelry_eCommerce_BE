const { DataTypes } = require('sequelize');
const sequelize = require('../config/Database');

const UserRoleEnum = ['ADMIN', 'USER', 'MANAGER', 'DELIVERER'];
const UserStateEnum = ['ACTIVE', 'INACTIVE', 'BANNED'];

const User = sequelize.define('User', {
    id: {
        type: DataTypes.BIGINT,
        autoIncrement: true,
        primaryKey: true,
    },
    email: {
        type: DataTypes.STRING,
        allowNull: false,
        unique: true,
        validate: {
            notEmpty: {
                msg: 'Email không được để trống',
            },
            isEmail: {
                msg: 'Email không hợp lệ',
            },
        },
    },
    password: {
        type: DataTypes.STRING,
        allowNull: false,
        validate: {
            notEmpty: {
                msg: 'Mật khẩu không được để trống',
            },
        },
    },
    name: {
        type: DataTypes.STRING,
        allowNull: true,
    },
    age: {
        type: DataTypes.INTEGER,
        allowNull: true,
    },
    gender: {
        type: DataTypes.STRING,
        allowNull: true,
    },
    address: {
        type: DataTypes.STRING,
        allowNull: true,
    },
    avatar: {
        type: DataTypes.STRING,
        allowNull: true,
    },
    role_id: {
        type: DataTypes.BIGINT,
        allowNull: true,
    },
    refresh_token: {
        type: DataTypes.TEXT,
        allowNull: true,
    },
    reset_token: {
        type: DataTypes.STRING,
        allowNull: true,
    },
    created_at: {
        type: DataTypes.DATE,
        allowNull: false,
        defaultValue: DataTypes.NOW,
    },
    updated_at: {
        type: DataTypes.DATE,
        allowNull: true, // Chỉnh sửa từ false thành true
    },
    created_by: {
        type: DataTypes.STRING,
        allowNull: true,
    },
    updated_by: {
        type: DataTypes.STRING,
        allowNull: true,
    },
    state: {
        type: DataTypes.STRING,
        allowNull: true,
        defaultValue: 'ACTIVE',
        validate: {
            isIn: {
                args: [UserStateEnum],
                msg: `State must be one of: ${UserStateEnum.join(', ')}`
            }
        }
    },
    role: {
        type: DataTypes.STRING,
        allowNull: true,
        validate: {
            isIn: {
                args: [UserRoleEnum],
                msg: `Role must be one of: ${UserRoleEnum.join(', ')}`
            }
        }
    }
}, {
    tableName: 'users',
    timestamps: true,
    underscored: true,
});

module.exports = User;