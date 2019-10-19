const mongoose = require('mongoose');
mongoose.set('debug', true);
mongoose.Promise = Promise;
mongoose.connect('mongodb://localhost:27017/acme', { useNewUrlParser: true, useUnifiedTopology: true, useCreateIndex: true });

module.exports.User = require('./user');
module.exports.Product = require('./product');2
module.exports.PaymentCard = require('./paymentCard');
module.exports.ShoppingBasket = require('./shoppingBasket');
module.exports.Voucher = require('./voucher');
