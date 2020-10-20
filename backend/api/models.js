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
})

module.exports = {
    User: mongoose.model('User', userSchema)
};