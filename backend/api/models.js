const mongoose = require('mongoose');
const { Schema } = mongoose;

// User schema
const userSchema = new Schema({
    _id: String,
    name: { type: String, required: true },
    nif: { type: String, required: true },
    ccNumber: { type: String, required: true },
    ccExpiration: { type: String, required: true },
    ccCVV: { type: String, required: true },
    username: { type: String, unique: true, required: true },
    password: { type: String, required: true },
    publicKey: { type: String, required: true },
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
    name: { type: String, required: true },
    price: { type: Number, required: true },
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