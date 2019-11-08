const mongoose = require('mongoose');
const uuidv4 = require('uuid/v4');
require('mongoose-uuid2')(mongoose);

const productSchema = new mongoose.Schema({
  _id: { type: mongoose.Types.UUID, default: uuidv4 },
  name: {
    type: String,
    required: true
  },
  price: {
    type: Number,
    required: true
  },
  qr_code: {
    type: String,
    required: true
  },
  shopping_baskets: [
    {
      type: mongoose.Types.UUID,
      ref: 'ShoppingBasket'
    }
  ]
});

const Product = mongoose.model('Product', productSchema);

module.exports = Product;
