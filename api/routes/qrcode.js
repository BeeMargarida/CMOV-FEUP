const express = require('express');
const router = express.Router();
const { customQRCode, peachQRCode, mangaQRCode, melonQRCode, lettuceQRCode, potatoQRCode, tomatoQRCode, cornQRCode, chichenQRCode } = require('../controllers/qrcode');

router.get('/custom', customQRCode);
router.get('/peach', peachQRCode);
router.get('/manga', mangaQRCode);
router.get('/melon', melonQRCode);
router.get('/lettuce', lettuceQRCode);
router.get('/potato', potatoQRCode);
router.get('/tomato', tomatoQRCode);
router.get('/corn', cornQRCode);
router.get('/chicken', chichenQRCode);

module.exports = router;
