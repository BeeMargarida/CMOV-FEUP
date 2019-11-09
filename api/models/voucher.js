const mongoose = require('mongoose');
const uuidv4 = require('uuid/v4');
require('mongoose-uuid2')(mongoose);

const voucherSchema = new mongoose.Schema({
  _id: { type: mongoose.Types.UUID, default: uuidv4 },
  user: {
    type: mongoose.Types.UUID,
    ref: 'User'
  }
});

const Voucher = mongoose.model('Voucher', voucherSchema);

module.exports = Voucher;
