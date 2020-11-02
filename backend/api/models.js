const mongoose = require('mongoose');
const { Schema } = mongoose;

// User schema
const userSchema = new Schema({
    _id: String,
    name: { type: String, required: true },
    nif: { type: String, required: true },
    ccNumber: { type: String, required: true },
    ccCVV: { type: String, required: true },
    ccExpiration: { type: String, required: true },
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
    type: { type: String, enum: ['food', 'drink', 'coffee'], required: true },
    name: { type: String, required: true },
    price: { type: Number, required: true }
}, {
    timestamps: true,
    toJSON: {
        transform: (doc, ret) => {
            ret.id = ret._id;
            delete ret._id;
            delete ret.__v;
            delete ret.createdAt;
            delete ret.updatedAt;
            return ret;
        }
    }
});

const voucherSchema = new Schema({
    _id: String,
    userId: { type: String, required: true },
    type: { type: String, enum: ['o', 'd'], required: true }, // O - Offer, D - Discount
    used: { type: Boolean, default: false, required: true }
}, {
    timestamps: true,
    toJSON: {
        transform: (doc, ret) => {
            ret.id = ret._id;
            delete ret._id;
            delete ret.__v;
            delete ret.userId;
            delete ret.used;
            delete ret.createdAt;
            delete ret.updatedAt;
            return ret;
        }
    }
})

module.exports = {
    User: mongoose.model('User', userSchema),
    Item: mongoose.model('Item', itemSchema),
    Voucher: mongoose.model('Voucher', voucherSchema)
};