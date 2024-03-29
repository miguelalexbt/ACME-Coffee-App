const express = require('express');
const mongoose = require('mongoose');
const app = express();

const { authRouter, itemRouter, voucherRouter, orderRouter, imageRouter } = require('./api/routes');

let isDbReady = false;

// Connect to MongoDB
mongoose
    .connect(`mongodb://${process.env.MONGO_USERNAME}:${process.env.MONGO_PASSWORD}@mongo:27017/${process.env.MONGO_DB}`, 
        { useNewUrlParser: true, useUnifiedTopology: true, useCreateIndex: true, useFindAndModify: false }
    )
    .then(() => isDbReady = true)
    .catch((err) => console.log(err));

// Global middleware
app.use(express.json({
    verify: (req, res, buf, encoding) => {
        req.rawBody = buf
    }
}));

app.use(express.urlencoded({ extended: true }));

// Wait for DB connection
app.use((req, res, next) => {
    isDbReady ? next() : res.status(500).send('An error has occurred!');
});

// Routes
app.use('/', authRouter);
app.use('/items', itemRouter);
app.use('/vouchers', voucherRouter);
app.use('/orders', orderRouter);
app.use('/image', imageRouter);

// Listen
const port = 3000;
app.listen(port, () => console.log('Server running...' + process.env.MONGO_USERNAME));