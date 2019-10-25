const express = require('express');
const router = express.Router();
const { checkoutBasket, listBaskets } = require('../controllers/supermarket');

router.post('/checkout', checkoutBasket);
router.get('/list', listBaskets);
router.get('/vouchers', listVouchers);

module.exports = router;
