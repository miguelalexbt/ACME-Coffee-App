let express = require('express')
const { body, validationResult } = require('express-validator');
const { v4: uuidv4 } = require('uuid');
const bcrypt = require('bcrypt');

const { User } = require('./models');

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

const authenticated = (req, res, next) => {

    // CHEcK IF AUTHENTICATED
}

// Auth
let authRouter = express.Router()

authRouter.post('/signIn', async (req, res) => {
    const body = req.body

    console.log(body)

    const user = await User.findOne({ username: body.username }).exec()

    if (user !== null)
        res.json(user)
    else
        res.status(401)
});

authRouter.post('/signUp', async (req, res) => {
    const body = req.body

    console.log(body)

    const user = new User({
        _id: uuidv4(),
        name: body.user.name,
        username: body.user.username,
        password: await bcrypt.hash(body.user.password, 10),
        certificate: body.certificate
    });

    await user.save();

    res.json(user);
});

// Items

let itemRouter = express.Router()

itemRouter.get('/', (req, res) => {
	

})

module.exports = { 
    authRouter: authRouter,
};