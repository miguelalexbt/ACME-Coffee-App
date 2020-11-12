const mongoose = require('mongoose');
const AutoIncrement = require('mongoose-sequence')(mongoose);
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
    consumedCoffees: { type: Number,  default: 0, required: true },
    accumulatedPayedValue: { type: Number, default: 0, required: true }
}, {
    toJSON: {
        transform: (doc, ret) => {
            ret.id = ret._id;
            delete ret._id;
            delete ret.__v;
            delete ret.consumedCoffees;
            delete ret.accumulatedPayedValue;
            return ret;
        }
    }
});

// Item schema
const itemSchema = new Schema({
    type: { type: String, enum: ['food', 'drink', 'coffee', 'breakfast', 'pastry'], required: true },
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

// Voucher schema
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
            delete ret.used;
            delete ret.createdAt;
            delete ret.updatedAt;
            return ret;
        }
    }
});

// Order schema
const orderSchema = new Schema({
    userId: { type: String, required: true },
    items: { type: Map, of: Number, required: true },
    vouchers: { type: Array, of: String, required: true },
    total: { type: Number, required: true }
}, {
    timestamps: true,
    toJSON: {
        transform: (doc, ret) => {
            ret.id = ret._id;
            delete ret._id;
            delete ret.__v;
            delete ret.updatedAt;
            return ret;
        }
    }
});

orderSchema.plugin(AutoIncrement, { inc_field: 'number' })

module.exports = {
    User: mongoose.model('User', userSchema),
    Item: mongoose.model('Item', itemSchema),
    Voucher: mongoose.model('Voucher', voucherSchema),
    Order: mongoose.model('Order', orderSchema)
};