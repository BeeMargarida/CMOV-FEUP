const mongoose = require('mongoose');
const uuidv4 = require('uuid/v4');
require('mongoose-uuid2')(mongoose);
const User = require('./user');

const paymentCardSchema = new mongoose.Schema(
  {
    _id: { type: mongoose.Types.UUID, default: uuidv4 },
    name: {
      type: String,
      required: true,
      maxlength: 160
    },
    number: {
      type: String,
      required: true,
      maxlength: 16
    },
    valid_date: {
      type: Date,
      required: true
    },
    cvc: {
      type: String,
      required: true,
      maxlength: 3
    },
    user: {
      type: mongoose.Types.UUID,
      ref: 'User'
    }
  },
  {
    timestamps: true
  }
);

paymentCardSchema.pre('remove', async function(next) {
  try {
    let user = await User.findById(this.user);
    user.payment_card.remove(this.id);
    await user.save();
    return next();
  } catch (err) {
    return next(err);
  }
});

const PaymentCard = mongoose.model('PaymentCard', paymentCardSchema);

module.exports = PaymentCard;
