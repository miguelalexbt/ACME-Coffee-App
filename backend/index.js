const express = require('express');
const mongoose = require('mongoose');
const app = express();

const { authRouter } = require('./api/routes');

let isDbReady = false;

// Connect to MongoDB
mongoose
    .connect(`mongodb://${process.env.MONGO_USERNAME}:${process.env.MONGO_PASSWORD}@mongo:27017/${process.env.MONGO_DB}`, 
        { useNewUrlParser: true, useUnifiedTopology: true }
    )
    .then(() => isDbReady = true)
    .catch((err) => console.log(err));

// Global middleware
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Wait for DB connection
app.use((req, res, next) => {
    isDbReady ? next() : res.status(500).send('An error has occurred!');
});

// Routes
app.use('/auth', authRouter);
// app.use('/customer', customerRouter);

// Listen
const port = 3000;
app.listen(port, () => console.log('Server running...' + process.env.MONGO_USERNAME));