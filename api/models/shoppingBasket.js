const mongoose = require('mongoose');
const uuidv4 = require('uuid/v4');
require('mongoose-uuid2')(mongoose);
const User = require('./user');

const shoppingBasketSchema = new mongoose.Schema(
  {
    _id: { type: mongoose.Types.UUID, default: uuidv4 },
    products: [
      {
        type: mongoose.Types.UUID,
        ref: 'Product'
      }
    ],
    user: {
      type: mongoose.Types.UUID,
      ref: 'User'
    },
    total_price: {
      type: Number,
      required: true
    },
    paid_price: {
      type: Number,
      required: true
    }
  },
  {
    timestamps: true
  }
);

shoppingBasketSchema.pre('remove', async function(next) {
  try {
    let user = await User.findById(this.user);
    user.shopping_baskets.remove(this.id);
    await user.save();
    return next();
  } catch (err) {
    return next(err);
  }
});

const ShoppingBasket = mongoose.model('ShoppingBasket', shoppingBasketSchema);

module.exports = ShoppingBasket;
