const express = require('express');
const router = express.Router();
const { peachQRCode, mangaQRCode } = require('../controllers/qrcode');

router.get('/peach', peachQRCode);
router.get('/manga', mangaQRCode);

module.exports = router;
