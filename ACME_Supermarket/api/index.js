require('dotenv').config();
const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const nodeRSA = require('node-rsa');
const errorHandler = require('./controllers/error');
const authRoutes = require('./routes/auth');
const supermarketRoutes = require('./routes/supermarket');
const qrCodeRoutes = require('./routes/qrcode');
const db = require('./models');

const PORT = 8080;
const app = express();

// TODO: Get supermarket public key
if(!process.env.SUPERMARKET_PUBLIC_KEY || !process.env.SUPERMARKET_PRIVATE_KEY){
  const key = new nodeRSA({b: 512});
  process.env.SUPERMARKET_PUBLIC_KEY = key.exportKey('public');
  process.env.SUPERMARKET_PRIVATE_KEY = key.exportKey('private');
}
console.log(process.env.SUPERMARKET_PUBLIC_KEY);
console.log(process.env.SUPERMARKET_PRIVATE_KEY);

app.use(cors());
app.use(bodyParser.json());
app.set("view engine", "ejs");
app.use(express.static(__dirname + "/public"));

app.use('/auth', authRoutes);
app.use('/supermarket', supermarketRoutes);

//TODO: Add User routes
//TODO: Add Payment Card routes ? -> check if needed

app.use('/qrcode', qrCodeRoutes);

app.use('/', async (req, res, next) => {
  await db.Product.find({})
  .then((products) => {
    console.log(products);
    res.render("index", {products: products, page: 'index'});
  })
  .catch((error) => next(error));
});

app.use(function(req, res, next) {
  let err = new Error('Not Found');
  err.status = 404;
  next(err);
});

app.use(errorHandler);

app.listen(PORT, function() {
  console.log(`Server is starting on port ${PORT}`);
});
