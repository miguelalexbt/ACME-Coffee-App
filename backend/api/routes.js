let express = require('express')
const { body, validationResult } = require('express-validator');
const { v4: uuidv4 } = require('uuid');
const crypto = require('crypto')

const fs = require("fs");
const path = require("path")
const { User, Item, Voucher } = require('./models');

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
    await new Item({ name: 'Americano', type: 'drink', price: 0.80 }).save();
    await new Item({ name: 'Coca-Cola', type: 'drink', price: 1.00 }).save();
    await new Item({ name: 'Latte', type: 'drink', price: 3.99 }).save();
    await new Item({ name: 'Mochaccino', type: 'drink', price: 4.99 }).save();
    await new Item({ name: 'Cappuccino', type: 'drink', price: 2.99 }).save();
    await new Item({ name: 'Croissant', type: 'drink', price: 2.50 }).save();
    await new Item({ name: 'Water', type: 'drink', price: 0.99 }).save();
    await new Item({ name: 'Expresso', type: 'drink', price: 0.70 }).save();

    res.sendStatus(200);
})

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
})

// Vouchers

let voucherRouter = express.Router();

voucherRouter.get('/populate', async (req, res) => {
    await new Voucher({
        _id: uuidv4(),
        userId: '0a8b6dc8-d0c2-4233-8dd9-6d0d8cac4536',
        type: 'o'
    }).save();

    res.sendStatus(200);
})

voucherRouter.get('/', authenticateClientRequest, async (req, res) => {
    const vouchers = await Voucher.find({
        userId: req.query.userId,
        used: false
    }).exec()

    res.json(vouchers);
});

// Orders

let orderRouter = express.Router();

orderRouter.put('/', authenticateTerminalRequest, async (req, res) => {
    const order = req.body.order;

    console.log("ORDER ", order)

    // Validate items

    // Validate vouchers
    // - Check non applicable -> ignore
    // - Check invalid -> delete
    

    res.json("HELLo");
});

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
})

module.exports = { 
    authRouter: authRouter,
    itemRouter: itemRouter,
    voucherRouter: voucherRouter,
    orderRouter: orderRouter,
    imageRouter: imageRouter
};
