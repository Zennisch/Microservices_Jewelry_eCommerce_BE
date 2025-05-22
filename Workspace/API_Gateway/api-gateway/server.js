require('dotenv').config();
const express = require('express');
const {createProxyMiddleware} = require('http-proxy-middleware');
const morgan = require('morgan');
const winston = require('winston');
const cors = require('cors');

// Configure Winston logger
const logger = winston.createLogger({
    level: 'info',
    format: winston.format.combine(winston.format.timestamp(), winston.format.json()),
    transports: [
        new winston.transports.File({filename: 'logs/error.log', level: 'error'}),
        new winston.transports.File({filename: 'logs/combined.log'}),
        new winston.transports.Console({
            format: winston.format.combine(winston.format.colorize(), winston.format.simple()),
        }),
    ],
});

// Create Express app
const app = express();
const PORT = process.env.PORT || 8000;

// Enable CORS
app.use(cors());

// Middleware for request logging
app.use(morgan('combined'));

// Custom middleware to log request details
app.use((req, res, next) => {
    const startTime = Date.now();

    // Log request
    logger.info({
        type: 'request',
        method: req.method,
        url: req.url,
        headers: req.headers,
        query: req.query,
        timestamp: new Date().toISOString(),
    });

    // Capture and log response
    const originalSend = res.send;
    res.send = function (body) {
        const responseTime = Date.now() - startTime;

        logger.info({
            type: 'response',
            method: req.method,
            url: req.url,
            statusCode: res.statusCode,
            responseTime: `${responseTime}ms`,
            timestamp: new Date().toISOString(),
        });

        return originalSend.call(this, body);
    };

    next();
});

// Define route for API health check
app.get('/health', (req, res) => {
    res.status(200).json({status: 'UP', timestamp: new Date().toISOString()});
});

// Proxy middleware for Service Account
app.use(
    '/api/v1/account',
    createProxyMiddleware({
        target: 'http://localhost:8001/api/v1',
        changeOrigin: true,
        pathRewrite: {
            '^/api/v1/account': '', // Remove the /api/v1/account prefix when forwarding
        },
        onProxyReq: (proxyReq, req, res) => {
            logger.debug(`Proxying request to Service Account: ${req.method} ${req.url}`);
        },
        onProxyRes: (proxyRes, req, res) => {
            logger.debug(`Received response from Service Account: ${proxyRes.statusCode}`);
        },
    })
);

// Proxy middleware for Service Manager
app.use(
    '/api/v1/manager',
    createProxyMiddleware({
        target: 'http://localhost:8003',
        changeOrigin: true,
        pathRewrite: {
            '^/api/v1/manager': '', // Remove the /api/v1/manager prefix when forwarding
        },
        onProxyReq: (proxyReq, req, res) => {
            logger.debug(`Proxying request to Service Manager: ${req.method} ${req.url}`);
        },
        onProxyRes: (proxyRes, req, res) => {
            logger.debug(`Received response from Service Manager: ${proxyRes.statusCode}`);
        },
    })
);

// Proxy middleware for Service Catalog
app.use(
    '/api/v1/catalog',
    createProxyMiddleware({
        target: 'http://localhost:8005/api',
        changeOrigin: true,
        pathRewrite: {
            '^/api/v1/catalog': '', // Remove the /api/v1/catalog prefix when forwarding
        },
        onProxyReq: (proxyReq, req, res) => {
            logger.debug(`Proxying request to Service Catalog: ${req.method} ${req.url}`);
        },
        onProxyRes: (proxyRes, req, res) => {
            logger.debug(`Received response from Service Catalog: ${proxyRes.statusCode}`);
        },
    })
);

// Proxy middleware for Service Cart Order
app.use(
    '/api/v1/cart-order',
    createProxyMiddleware({
        target: 'http://localhost:8006/api',
        changeOrigin: true,
        pathRewrite: {
            '^/api/v1/cart-order': '', // Remove the /api/v1/cart-order prefix when forwarding
        },
        onProxyReq: (proxyReq, req, res) => {
            logger.debug(`Proxying request to Service Cart Order: ${req.method} ${req.url}`);
        },
        onProxyRes: (proxyRes, req, res) => {
            logger.debug(`Received response from Service Cart Order: ${proxyRes.statusCode}`);
        },
    })
);

// Handle 404 routes
app.use((req, res) => {
    logger.warn(`Route not found: ${req.method} ${req.originalUrl}`);
    res.status(404).json({error: 'Not Found', path: req.originalUrl});
});

// Error handling middleware
app.use((err, req, res, next) => {
    logger.error(`Error processing request: ${err.message}`, {stack: err.stack});
    res.status(500).json({error: 'Internal Server Error'});
});

// Start server
app.listen(PORT, () => {
    logger.info(`API Gateway running on http://localhost:${PORT}`);
});