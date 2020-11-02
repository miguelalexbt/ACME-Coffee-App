let express = require('express')
const { body, validationResult } = require('express-validator');
const { v4: uuidv4 } = require('uuid');
const crypto = require('crypto')

const fs = require("fs");
const path = require("path")
const { User, Item, Voucher } = require('./models');

// Validation
const validate = validations => {
    return async (req, res, next) => {
        await Promise.all(validations.map(validation => validation.run(req)));
    
        const errors = validationResult(req);
        if (errors.isEmpty()) {
          return next();
        }
    
        res.status(400).json({ errors: errors.array() });
      };
};

const authenticateRequest = async (req, res, next) => {
    if (!('user-signature' in req.headers)) {
        res.status(403).json({ error: 'missing_signature' });
        return;
    }

    const dateHeader = req.header('Date');
    const timestamp = new Date(dateHeader);

    const authHeader = req.header('User-Signature');
    const [userId, signature] = authHeader.split(':');

    const user = await User.findById(userId).exec();

    if (!user) {
        res.status(403).json({ error: 'invalid_signature' });
        return;
    }

    const publicKey = `-----BEGIN PUBLIC KEY-----\n${user.publicKey}\n-----END PUBLIC KEY-----`;

    let verifiableData = userId + req.originalUrl + dateHeader + (req.rawBody ? req.rawBody.toString() : "")

    const verifier = crypto.createVerify('RSA-SHA256');
    verifier.update(verifiableData, 'utf-8');

    const isValid = verifier.verify(publicKey, signature, 'base64');

    if (!isValid) {
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

// Auth
let authRouter = express.Router()

authRouter.post('/signUp', async (req, res) => {
    const body = req.body;

    const user = new User({
        _id: uuidv4(),
        name: body.name,
        nif: body.nif,
        ccNumber: body.ccNumber,
        ccExpiration: body.ccExpiration,
        ccCVV: body.ccCVV,
        publicKey: body.publicKey
    });

    await user.save();

    res.json({ userId: user._id });
});

// Items

let itemRouter = express.Router()

itemRouter.get('/populate', async (req, res) => {
    await new Item({ name: 'Sandwich', type: 'food', price: 4.5}).save();
    await new Item({ name: 'Pastry', type: 'food', price: 2.3 }).save();
    await new Item({ name: 'Coffee', type: 'drink', price: 0.6 }).save();
    await new Item({ name: 'Coca-Cola', type: 'drink', price: 1.0 }).save();
    await new Item({ name: 'Latte', type: 'drink', price: 3.99 }).save();
    await new Item({ name: 'Mochaccino', type: 'drink', price: 4.99 }).save();
    await new Item({ name: 'Cappuccino', type: 'drink', price: 2.99 }).save();
    await new Item({ name: 'Croissant', type: 'drink', price: 2.50 }).save();
    await new Item({ name: 'Water', type: 'drink', price: 0.99 }).save();
    await new Item({ name: 'Expresso', type: 'drink', price: 0.70 }).save();

    res.sendStatus(200);
})

itemRouter.get('/', authenticateRequest, async (req, res) => {
    let lastUpdateQuery = {};

    if (req.query.last_update) {
        const lastUpdate = new Date(req.query.last_update);

        lastUpdateQuery = {
            $or: [
                { createdAt: { $gte: lastUpdate } },
                { updatedAt: { $gte: lastUpdate } }
            ]
        }
    }

    const items = await Item.find(lastUpdateQuery).exec() 
    
    res.json(items);
})

// Vouchers

let voucherRouter = express.Router()

voucherRouter.get('/populate', async (req, res) => {
    await new Voucher({
        _id: uuidv4(),
        userId: 'e289241a-f5ed-42db-87b1-6e52eac5b3de',
        type: 'o'
    }).save();

    res.sendStatus(200);
})

voucherRouter.get('/', async (req, res) => {
    let lastUpdateQuery = {};

    if (req.query.last_update) {
        const lastUpdate = new Date(req.query.last_update);

        lastUpdateQuery = {
            $or: [
                { createdAt: { $gte: lastUpdate } },
                { updatedAt: { $gte: lastUpdate }}
            ]
        }
    }

    const vouchers = await Voucher.find({
        $and: [
            {
                userId: req.query.userId,
                used: false,
            },
            lastUpdateQuery
        ]
    }).exec() 

    res.json(vouchers);
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

    let imagePath = req.params.imagePath + '.png'
    fs.stat(`${__dirname}/../images/` + imagePath, function(errStat, stats) {
        if (errStat) {
            console.log(errStat)
            next(errStat)
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
    imageRouter: imageRouter
};
