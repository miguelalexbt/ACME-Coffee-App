const mongoose = require('mongoose');
const { Schema } = mongoose;

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
})

module.exports = {
    User: mongoose.model('User', userSchema)
};