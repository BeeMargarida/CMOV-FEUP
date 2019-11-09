const express = require('express');
const router = express.Router();
const { peachQRCode, mangaQRCode, melonQRCode, lettuceQRCode } = require('../controllers/qrcode');

router.get('/peach', peachQRCode);
router.get('/manga', mangaQRCode);
router.get('/melon', melonQRCode);
router.get('/lettuce', lettuceQRCode);

module.exports = router;
