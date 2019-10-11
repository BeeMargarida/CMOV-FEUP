const mongoose = require('mongoose');
const bcrypt = require('bcrypt');
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
  password: {
    type: String,
    required: true
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

userSchema.pre('save', async function(next) {
  try {
    if (!this.isModified('password')) {
      return next();
    }
    let hashedPassword = await bcrypt.hash(this.password, 10);
    this.password = hashedPassword;
    return next();
  } catch (err) {
    return next(err);
  }
});

userSchema.methods.comparePassword = async function(candidatePassword, next) {
  try {
    let isMatch = await bcrypt.compare(candidatePassword, this.password);
    return isMatch;
  } catch (err) {
    return next(err);
  }
};

const User = mongoose.model('User', userSchema);

module.exports = User;
