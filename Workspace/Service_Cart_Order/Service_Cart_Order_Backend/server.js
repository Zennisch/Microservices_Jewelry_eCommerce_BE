const express = require('express');
const syncDatabase = require('./src/config/syncDatabase');
const cartRoutes = require('./src/routers/client/cartRoutes'); 
const orderRoutesAdmin = require('./src/routers/admin/orderRoutesAdmin');
const orderRoutesClient = require('./src/routers/client/orderRoutersClient');
const paymentRoutes = require('./src/routers/client/paymentRouters');
const locationRoutes = require('./src/routers/client/locationRoutes');
const deliveryRouters = require('./src/routers/client/deliveryRoutes');
const cors = require("cors");

const app = express();

app.use(cors()); 
app.use(express.urlencoded({ extended: true }));

app.use(express.json());
app.use('/api', cartRoutes);
app.use('/api', orderRoutesAdmin);
app.use('/api', orderRoutesClient);
app.use('/api/payment', paymentRoutes);
app.use('/api/location', locationRoutes);
app.use('/api/delivery', deliveryRouters);

// Chỉ gọi `sequelize.sync()` tại đây
syncDatabase()
    .then(() => {
        console.log('Database synchronized...');
        
        app.listen(8006, () => {
            console.log('Server running at http://localhost:8006');
        });
    })
    .catch((error) => {
        console.error('Failed to synchronize database:', error);
    });