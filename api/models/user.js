const mongoose = require('mongoose');
const uuidv4 = require('uuid/v4');
require('mongoose-uuid2')(mongoose);

const userSchema = new mongoose.Schema({
  _id: { type: mongoose.Types.UUID, default: uuidv4 },
  email: {
    type: String,
    required: true,
    unique: true
  },
  name: {
    type: String,
    required: true
  },
  username: {
    type: String,
    required: true,
    unique: true
  },
  total_accumulated: {
    type: Number,
    required: true
  },
  public_key: {
    type: String,
    required: true
  },
  payment_card: {
    type: mongoose.Types.UUID,
    ref: 'PaymentCard'
  },
  shopping_baskets: [
    {
      type: mongoose.Types.UUID,
      ref: 'ShoppingBasket'
    }
  ],
  vouchers: [
    {
      type: mongoose.Types.UUID,
      ref: 'Voucher'
    }
  ]
});

const User = mongoose.model('User', userSchema);

module.exports = User;
