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

// Auth

let authRouter = express.Router()



// Customer

let customerRouter = express.Router()

customerRouter.put('/', validate([
        // body('name').exists({ checkNull: true, checkFalsy: true }),
        // body('username').exists({ checkNull: true, checkFalsy: true }),
        // body('password').exists({ checkNull: true, checkFalsy: true })
    ]),
    async (req, res) => {
        const body = req.body

        const user = new User({
            _id: uuidv4(),
            name: body.name,
            username: body.username,
            password: await bcrypt.hash(body.password, 10),
            certificate: body.certificate
        });

        await user.save();

        res.json(user);
    }
);

module.exports = { customerRouter: customerRouter };