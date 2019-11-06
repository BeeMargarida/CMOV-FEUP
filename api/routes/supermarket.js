const express = require('express');
const router = express.Router();
const { checkoutBasket, listBaskets, listVouchers, getDiscount } = require('../controllers/supermarket');

router.post('/checkout', checkoutBasket);
router.get('/list', listBaskets);
router.get('/vouchers', listVouchers);
router.get('/discount', getDiscount);

module.exports = router;
