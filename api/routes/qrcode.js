const express = require('express');
const router = express.Router();
const { peachQRCode } = require('../controllers/qrcode');

router.get('/peach', peachQRCode);

module.exports = router;
