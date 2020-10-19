const mongoose = require('mongoose');
const bodyParser = require('body-parser');
const app = require('express')();

mongoose.connect('mongodb://mongo:27017/express-mongo', { useNewUrlParser: true, useUnifiedTopology: true })
    .then(() => console.log('MongoDB connected'))
    .catch((err) => console.log(err));

app.use(bodyParser.urlencoded({ extended: false }));

app.get('/', (req, res) =>{
    res.send("it's working :(")   
})

const port = 3000;
app.listen(port, () => console.log('Server running...'));