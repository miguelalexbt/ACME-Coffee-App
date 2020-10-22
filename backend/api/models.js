const mongoose = require('mongoose');
const { Schema } = mongoose;

// User schema
const userSchema = new Schema({
    _id: String,
    name: String,
    // NIF: 
    // credit/DEBIT:
    username: String,
    password: String,
    certificate: String
}, {
    toJSON: {
        transform: (doc, ret) => {
            ret.id = ret._id;
            delete ret._id;
            delete ret.__v;
            return ret;
        }
    }
});

// Item schema
const itemSchema = new Schema({
    type: { type: String, enum: ['food', 'drink'] },
    name: String,
    price: Number
}, {
    toJSON: {
        transform: (doc, ret) => {
            ret.id = ret._id;
            delete ret._id;
            delete ret.__v;
            return ret;
        }
    }
});

module.exports = {
    User: mongoose.model('User', userSchema),
    Item: mongoose.model('Item', itemSchema)
};