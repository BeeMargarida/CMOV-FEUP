const QRCode = require('qrcode');
const uuid = require('uuid/v4');
const nodeRSA = require('node-rsa');
const fs = require('fs');
const path = require('path');
const iconv = require("iconv-lite");
const db = require("../models");

exports.customQRCode = async function (req, res, next) {
    try {
        const name = req.query.name;
        const euros = parseInt(req.query.euros);
        const cents = parseInt(req.query.cents);
        generateQRCode(name, euros, cents, res, next);

    } catch (error) {
        next(error);
    }
}

exports.peachQRCode = async function (req, res, next) {
    generateQRCode("Peach", 0, 25, res, next);
}

exports.mangaQRCode = async function (req, res, next) {
    generateQRCode("Manga", 3, 0, res, next);
}

exports.melonQRCode = async function (req, res, next) {
    generateQRCode("Expensive Melon", 99, 0, res, next);
}

exports.lettuceQRCode = async function (req, res, next) {
    generateQRCode("Fresh Lettuce", 1, 0, res, next);
}

exports.potatoQRCode = async function (req, res, next) {
    generateQRCode("Potato", 0, 55, res, next);
}

exports.tomatoQRCode = async function (req, res, next) {
    generateQRCode("Tomato", 0, 33, res, next);
}

exports.cornQRCode = async function (req, res, next) {
    generateQRCode("Bonduelle Corn", 2, 24, res, next);
}

exports.chichenQRCode = async function (req, res, next) {
    generateQRCode("Friend Chicken", 6, 0, res, next);
}

function generateQRCode(name, priceEuros, priceCents, res, next) {
    const prod_uuid = new Array();
    uuid(null, prod_uuid, 0);
    console.log(prod_uuid);
    const prod_uuid_string = uuid({ random: prod_uuid });
    const len = 4 + 16 + 4 + 4 + 1 + name.length;
    if (name.length > 35) {
        name = name.substring(0, 35);
    }

    const tag = Buffer.alloc(len);
    tag.writeInt32BE(0x41636D65);
    const uuid_bytes_most = Buffer.from(prod_uuid).slice(0, 8).readBigUInt64LE();
    const uuid_bytes_less = Buffer.from(prod_uuid).slice(8, 16).readBigUInt64LE();
    tag.writeBigUInt64LE(uuid_bytes_most, 4);
    tag.writeBigUInt64LE(uuid_bytes_less, 12);
    tag.writeInt32BE(priceEuros, 20);
    tag.writeInt32BE(priceCents, 24);
    tag[28] = name.length;
    iconv.encode(name, 'ISO-8859-1').copy(tag, 29, 0, name.length);

    const key = new nodeRSA();
    key.importKey(process.env.SUPERMARKET_PRIVATE_KEY, 'pkcs1-private')
    key.setOptions({ encryptionScheme: 'pkcs1' });
    const encrypted = key.encryptPrivate(tag, 'hex');

    QRCode.toDataURL(encrypted)
        .then(url => {
            var base64Data = url.replace(/^data:image\/png;base64,/, "");
            fs.writeFile(`public/QRCodes/${name}_${prod_uuid_string}.png`, base64Data, 'base64', function (err) {
                if (err) throw err
                db.Product.create({
                    _id: prod_uuid_string,
                    name: name,
                    price: priceEuros + 0.01 * priceCents,
                    qr_code: `QRCodes/${name}_${prod_uuid_string}.png`
                });
                return res.status(200).sendFile(path.join(__dirname, '../public/QRCodes', `${name}_${prod_uuid_string}.png`));
            });
        })
        .catch(err => {
            return next(err);
        })
}