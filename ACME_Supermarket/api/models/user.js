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
  accumulated_discount: {
    type: Number,
    required: true,
    default: 0,
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
      ref: 'ShoppingBasket',
      autopopulate: true
    }
  ],
  vouchers: [
    {
      type: mongoose.Types.UUID,
      ref: 'Voucher',
      autopopulate: true
    }
  ]
});

const User = mongoose.model('User', userSchema);

module.exports = User;
