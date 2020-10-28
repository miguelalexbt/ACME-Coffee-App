let express = require('express')
const { body, validationResult } = require('express-validator');
const { v4: uuidv4 } = require('uuid');
const bcrypt = require('bcrypt');
const crypto = require('crypto')

const { User, Item } = require('./models');

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
        res.status(403).json({ error: "authentication" });
        return;
    }

    const authHeader = req.header('User-Signature');
    const [userId, signature] = authHeader.split(':');

    const user = await User.findById(userId).exec();

    if (!user) {
        res.status(403).json({ error: "authentication" });
        return;
    }

    const publicKey = `-----BEGIN PUBLIC KEY-----\n${user.publicKey}\n-----END PUBLIC KEY-----`;

    let verifiableData = req.method === 'GET' ? userId : (req.rawBody ? req.rawBody.toString() : "");

    const verifier = crypto.createVerify('RSA-SHA256');
    verifier.update(verifiableData, 'utf-8');

    const isValid = verifier.verify(publicKey, signature, 'base64');

    if (!isValid) {
        res.status(403).json({ error: "authentication" });
        return;
    }

    next();
}

// Auth
let authRouter = express.Router()

authRouter.post('/signIn', async (req, res) => {
    const body = req.body;
    const user = await User.findOne({ username: body.username }).exec();

    if (user === null) {
        res.status(401).json("Username/password combination is incorrect");
        return;
    }

    await bcrypt.compare(body.password, user.password) ? 
        res.json(user) 
        :
        res.status(403).json("Username/password combination is incorrect");
});

authRouter.post('/signUp', async (req, res) => {
    const body = req.body;

    const user = new User({
        _id: uuidv4(),
        name: body.name,
        nif: body.nif,
        ccNumber: body.ccNumber,
        ccExpiration: body.ccExpiration,
        ccCVV: body.ccCVV,
        username: body.username,
        password: bcrypt.hashSync(body.password, bcrypt.genSaltSync()),
        publicKey: body.publicKey
    });

    await user.save();

    res.json(user);
});

// Items

let itemRouter = express.Router()

itemRouter.get('/', async (req, res) => {
    let allItems = await Item.find({}).exec()
    res.json(allItems)
    // res.send(__dirname)
})

module.exports = { 
    authRouter: authRouter,
    itemRouter: itemRouter
};