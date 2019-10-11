const express = require('express');
const router = express.Router();
const { checkoutBasket, listBaskets } = require('../controllers/supermarket');

router.post('/checkout', checkoutBasket);
router.get('/list', listBaskets);

module.exports = router;
