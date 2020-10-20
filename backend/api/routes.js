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

// Customer

let customerRouter = express.Router()

// DEBUG
customerRouter.get('/', (req, res) => {
    res.send(`
    <!DOCTYPE html>
    <html>
    <body>
    <form method="post" action="/customer">
      <label for="name">Name:</label><br>
      <input type="text" id="name" name="name"><br>
      <label for="username">Username:</label><br>
      <input type="text" id="username" name="username"><br>
      <label for="password">Password:</label><br>
      <input type="password" id="password" name="password"><br>
      <input type="submit" value="Submit">
    </form> 
    </body>
    </html>`);
});

customerRouter.post('/', validate([
        body('name').exists({ checkNull: true, checkFalsy: true }),
        body('username').exists({ checkNull: true, checkFalsy: true }),
        body('password').exists({ checkNull: true, checkFalsy: true })
    ]),
    async (req, res) => {
        const body = req.body

        const user = new User({
            _id: uuidv4(),
            name: body.name,
            username: body.username,
            password: await bcrypt.hash(body.password, 10),
        });

        await user.save();

        res.json({ user_id: user._id });
    }
);

module.exports = { customerRouter: customerRouter };