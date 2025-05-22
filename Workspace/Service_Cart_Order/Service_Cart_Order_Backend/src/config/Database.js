const { Sequelize } = require('sequelize');

const sequelize = new Sequelize(
    process.env.DB_NAME || 'JEC',
    process.env.DB_USER || 'jec_admin', 
    process.env.DB_PASSWORD || 'jec_admin', 
    {
        host: process.env.DB_HOST || 'localhost',
        port: parseInt(process.env.DB_PORT || '6543'), 
        dialect: 'postgres',
        dialectOptions: {
            ssl: false,
        },
        logging: false,
        pool: {
            max: 5,
            min: 0,
            acquire: 30000,
            idle: 10000
        }
    }
);

sequelize.authenticate()
    .then(() => console.log(`PostgreSQL database connected to ${process.env.DB_HOST || 'localhost'}:${process.env.DB_PORT || '6543'}...`))
    .catch(err => console.error('PostgreSQL connection error:', err));

module.exports = sequelize;