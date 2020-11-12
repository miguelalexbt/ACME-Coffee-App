const mongoose = require('mongoose');
let express = require('express')
const { body, validationResult } = require('express-validator');
const { v4: uuidv4 } = require('uuid');
const crypto = require('crypto')

const fs = require("fs");
const path = require("path")
const { User, Item, Voucher, Order } = require('./models');

// Validation
// const validate = validations => {
//     return async (req, res, next) => {
//         await Promise.all(validations.map(validation => validation.run(req)));
    
//         const errors = validationResult(req);
//         if (errors.isEmpty()) {
//           return next();
//         }
    
//         res.status(400).json({ errors: errors.array() });
//       };
// };

const verifySignature = async (userId, signature, verifiableData) => {
    const user = await User.findById(userId).exec();

    if (!user)
        return false;

    const publicKey = `-----BEGIN PUBLIC KEY-----\n${user.publicKey}\n-----END PUBLIC KEY-----`;

    const verifier = crypto.createVerify('RSA-SHA256');
    verifier.update(verifiableData, 'utf-8');

    return verifier.verify(publicKey, signature, 'base64');
}

const authenticateClientRequest = async (req, res, next) => {
    if (!('user-signature' in req.headers)) {
        res.status(403).json({ error: 'missing_signature' });
        return;
    }

    const dateHeader = req.header('Date');
    const timestamp = new Date(dateHeader);

    const authHeader = req.header('User-Signature');
    const [userId, signature] = authHeader.split(':');

    let verifiableData = userId + req.originalUrl + dateHeader + (req.rawBody ? req.rawBody.toString() : '');

    if (!verifySignature(userId, signature, verifiableData)) {
        res.status(403).json({ error: 'invalid_signature' });
        return;
    }

    const requestAge = (new Date().getTime() - timestamp.getTime()) / 60000
    if (requestAge > 1) {
        res.status(401).json({ error: 'expired_timestamp' });
        return;
    }

    next();
}

const authenticateTerminalRequest = async (req, res, next) => {
    if (!('user-signature' in req.headers)) {
        res.status(403).json({ error: 'missing_signature' });
        return;
    }

    const authHeader = req.header('User-Signature');
    const [userId, signature] = authHeader.split(':');

    let verifiableData = req.body.order + '#' + userId;

    if (!verifySignature(userId, signature, verifiableData)) {
        res.status(403).json({ error: 'invalid_signature' });
        return;
    }

    next()
}

// Auth
let authRouter = express.Router();

authRouter.post('/signUp', async (req, res) => {
    const body = req.body;

    const user = new User({
        _id: uuidv4(),
        name: body.name,
        nif: body.nif,
        ccNumber: body.ccNumber,
        ccCVV: body.ccCVV,
        ccExpiration: body.ccExpiration,
        publicKey: body.publicKey
    });

    await user.save();

    res.json({ userId: user._id });
});

// Items

let itemRouter = express.Router();

itemRouter.get('/populate', async (req, res) => {
    await new Item({ name: 'Sandwich', type: 'food', price: 4.50 }).save();
    await new Item({ name: 'Pastry', type: 'food', price: 2.30 }).save();
    await new Item({ name: 'Americano', type: 'coffee', price: 0.80 }).save();
    await new Item({ name: 'Coca-Cola', type: 'drink', price: 1.00 }).save();
    await new Item({ name: 'Latte', type: 'coffee', price: 3.99 }).save();
    await new Item({ name: 'Mochaccino', type: 'coffee', price: 4.99 }).save();
    await new Item({ name: 'Cappuccino', type: 'coffee', price: 2.99 }).save();
    await new Item({ name: 'Croissant', type: 'food', price: 2.50 }).save();
    await new Item({ name: 'Water', type: 'drink', price: 0.99 }).save();
    await new Item({ name: 'Expresso', type: 'coffee', price: 0.70 }).save();

    res.sendStatus(200);
});

itemRouter.get('/', authenticateClientRequest, async (req, res) => {
    let lastUpdateQuery = {};

    if (req.query.lastUpdate) {
        const lastUpdate = new Date(req.query.lastUpdate);

        lastUpdateQuery = {
            $or: [
                { createdAt: { $gte: lastUpdate } },
                { updatedAt: { $gte: lastUpdate } }
            ]
        }
    }

    const items = await Item.find(lastUpdateQuery).exec();
    
    res.json(items);
});

// Vouchers

let voucherRouter = express.Router();

voucherRouter.get('/populate', async (req, res) => {

    const userId = 'a22b6777-c05b-48a2-9c55-862ab16f093d';

    await new Voucher({
        _id: uuidv4(),
        userId: userId,
        type: 'o'
    }).save();

    await new Voucher({
        _id: uuidv4(),
        userId: userId,
        type: 'o'
    }).save();


    await new Voucher({
        _id: uuidv4(),
        userId: userId,
        type: 'd'
    }).save();

    await new Voucher({
        _id: uuidv4(),
        userId: userId,
        type: 'd'
    }).save();


    res.sendStatus(200);
});

voucherRouter.get('/', authenticateClientRequest, async (req, res) => {
    const vouchers = await Voucher.find({
        userId: req.query.userId,
        used: false
    }).exec()

    res.json(vouchers);
});

// Orders

let orderRouter = express.Router();

orderRouter.get('/:userId', authenticateClientRequest, async (req, res) => {
    const orders = await Order.find({
        userId: req.params.userId
    }).exec();

    res.json(orders);
});

orderRouter.get('/:orderId/receipt', authenticateClientRequest, async (req, res) => {
    const order = await Order.findById(req.params.orderId).exec();
    const user = await User.findById(order.userId).exec();

    res.json({ nif: user.nif, ccNumber: user.ccNumber });
});

orderRouter.put('/', authenticateTerminalRequest, async (req, res) => {
    const userId = req.header('User-Signature').split(':')[0]
    const order = req.body.order;

    let itemsOrder = {};
    let vouchersOrder = [];

    // Parse items and vouchers
    order.split(';').forEach(i => {
        const [a, b] = i.split(':')
        b !== undefined ? itemsOrder[a] = parseInt(b) : vouchersOrder.push(a)
    });

    // Check items validity
    let validItems = (await Item
        .where('_id').in(Object.keys(itemsOrder))
        .select('_id type price'))
        .map(item => 
            ({ _id: item._id, type: item.type, price: item.price, quantity: itemsOrder[item.id] })    
        );

    if (Object.keys(itemsOrder).length !== validItems.length)  {
        res.status(422).json({ error: 'invalid_items' });
        return;
    }

    // Check vouchers validity
    let validVouchers = await Voucher
        .where('_id').in(vouchersOrder)
        .where('userId').equals(userId)
        .where('used').equals(false)
        .select('_id type');

    if (vouchersOrder.length !== validVouchers.length) {
        res.status(422).json({ error: 'invalid_vouchers' });
        return;
    }

    // Check vouchers applicability (ignore if not applicable)

    // - Only one discount voucher can be used
    let hasDiscountVoucher = false;
    validVouchers = validVouchers.filter(voucher =>
        voucher.type !== 'd' || !hasDiscountVoucher && (hasDiscountVoucher = true, true)
    );

    // - One offer voucher per coffee
    const coffeeItems = validItems.reduce((acc, items) => acc + (items.type === 'coffee'), 0);
    let coffeeVouchers = 0;
    validVouchers = validVouchers.filter(voucher =>
        voucher.type !== 'o' || coffeeVouchers < coffeeItems && (coffeeVouchers++, true)
    );

    const newOrder = await createOrder(userId, validItems, validVouchers);

    res.json({ orderNr: newOrder.number });
});

const createOrder = async (userId, items, vouchers) =>  {
    const coffeeVouchers = vouchers.filter(voucher => voucher.type === 'o').length;
    const hasDiscountVoucher = vouchers.some(voucher => voucher.type === 'd');

    // Ignore as many coffee items as coffee vouchers and calculate total
    let total = items
        .filter(item => (item !== "coffee" || (!(coffeeVouchers > 0) || (coffeeVouchers--, false))))
        .reduce((acc, item) => acc + item.price * item.quantity, 0.0);

    // Apply discount voucher
    if (hasDiscountVoucher)
        total *= 0.95;

    const itemsMap = items.reduce((acc, item) => { acc[item._id] = item.quantity; return acc;}, {});
    const vouchersArr = vouchers.map(voucher => voucher.id);

    // Start transaction, create order and update vouchers
    const session = await mongoose.startSession();
    session.startTransaction();

    const user = await User.findById(userId);

    // - Create order
    const order = new Order({
        userId: userId,
        items: itemsMap,
        vouchers: vouchersArr,
        total: parseFloat(total.toFixed(2))
    });

    await order.save();

    // - Remove used vouchers
    await Voucher
        .where('_id').in(vouchersArr)
        .updateMany({ 'used': true });
        
    // - Add new vouchers
    user.consumedCoffees += items.reduce((acc, item) => acc + (item.type === 'coffee' ? item.quantity : 0), 0);
    user.accumulatedPayedValue = parseFloat((user.accumulatedPayedValue + total).toFixed(2));

    // -- Offer vouchers
    const newCoffeeVouchers = Math.floor(user.consumedCoffees / 3);
    user.consumedCoffees -= newCoffeeVouchers * 3;
    await Promise.all([...Array(newCoffeeVouchers).keys()].map(_ => {
        return (new Voucher({ _id: uuidv4(), userId: userId, type: 'o' })).save();
    }));

    // -- Discount vouchers
    const newDiscountVouchers = Math.floor(user.accumulatedPayedValue / 100);
    user.accumulatedPayedValue -= newDiscountVouchers * 100;

    await Promise.all([...Array(newDiscountVouchers).keys()].map(_ => {
        return (new Voucher({ _id: uuidv4(), userId: userId, type: 'd' })).save();
    }));

    await user.save();

    await session.commitTransaction();
    session.endSession();

    return order;
}

// Images

let imageRouter = express.Router()

imageRouter.get('/:imagePath', async (req, res, next) => {
    const options = {
        root: `${__dirname}/../images/`,
        dotfiles: 'deny',
        headers: {
            'x-timestamp': Date.now(),
            'x-sent': true.valueOf(),
            'contentType': 'image/png'
        }
    }

    let imagePath = req.params.imagePath.toLowerCase() + '.png'
    fs.stat(`${__dirname}/../images/` + imagePath, function(errStat, stats) {
        if (errStat) {
            // console.log(errStat)
            res.set('Content-Type', 'text/plain');
            res.sendStatus(404);
        }
        else {
            res.status(200).sendFile(imagePath, options, function(err) {
                if (err) {
                    console.log(err)
                    next(err)
                } else {
                    console.log('Sent: ', imagePath)
                }
            })
        }
    })
});

module.exports = { 
    authRouter: authRouter,
    itemRouter: itemRouter,
    voucherRouter: voucherRouter,
    orderRouter: orderRouter,
    imageRouter: imageRouter
};
