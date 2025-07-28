const sequelize = require('./Database');
require('../models'); // Đảm bảo tất cả models đều được load

// Đảm bảo các model đã được đăng ký trước khi sync
const syncDatabase = async () => {
    try {
        // Cập nhật giá trị NULL thành giá trị thời gian hiện tại cho các trường updated_at
        // await sequelize.query(`UPDATE users SET updated_at = created_at WHERE updated_at IS NULL`);
        
        // Sau đó mới đồng bộ hóa schema
        await sequelize.sync({ force: true });
        console.log('PostgreSQL database & tables synchronized successfully!');
        return true;
    } catch (error) {
        console.error('Failed to sync database:', error);
        // Vẫn trả về true để server có thể tiếp tục chạy
        // Nhưng bạn có thể muốn xử lý lỗi khác nhau tùy vào trường hợp
        return true;
    }
};

module.exports = syncDatabase;