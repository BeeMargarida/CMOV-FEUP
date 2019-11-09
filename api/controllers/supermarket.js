const db = require('../models');
const nodeRSA = require('node-rsa');
const uuid = require('uuid/v4');

/**
 * Verifies if basket and request is valid, checks if payment is successful and creates a basket and saves it to user.
 * Updates user total accumulated.
 */
exports.checkoutBasket = async function (req, res, next) {
  try {
    console.log(req.body);
    const buffer = Buffer.from(req.body.basket, "latin1");
    const sizeBasket = buffer.readInt8();
    const basket = [];

    // Get Basket Products
    for (let i = 0; i < sizeBasket; i++) {
      let uuid_prod = Buffer.alloc(16);
      uuid_prod.writeBigUInt64LE(buffer.readBigUInt64LE(1 + i * 16));
      uuid_prod.writeBigUInt64LE(buffer.readBigUInt64LE(9 + i * 16), 8);
      let uuid_string = uuid({ random: [...uuid_prod] });

      await db.Product.findOne({ _id: uuid_string })
        .then((product) => {
          basket.push(product);
        })
        .catch((err) => next({ status: 400, message: `Product with id ${uuid_string} doesn't exist` }));
    }

    console.log(basket);

    // Get user UUID
    let uuid_user = Buffer.alloc(16);
    uuid_user.writeBigUInt64LE(buffer.readBigUInt64LE(1 + 16 * sizeBasket));
    uuid_user.writeBigUInt64LE(buffer.readBigUInt64LE(1 + 16 * sizeBasket + 8), 8);
    const uuid_user_string = uuid({ random: [...uuid_user] });
    console.log(uuid_user_string);

    let content = buffer.slice(0, 1 + 16 * sizeBasket + 16 + 1 + 16);
    let signature = buffer.slice(1 + 16 * sizeBasket + 16 + 1 + 16, buffer.length);

    await db.User.findOne({ _id: uuid_user_string })
      .then(async (foundUser) => {

        // Validate signature
        let result = validateSignature(content, signature, foundUser);
        if (!result) {
          next({ status: 401, message: "Signature invalid." })
        }

        const use_discount = buffer.readInt8(1 + 16 * sizeBasket + 16);

        const totalPrice = basket.reduce((acc, val) => (acc + val.price), 0);
        let finalPrice = totalPrice;
        if (use_discount) {
          finalPrice -= foundUser.accumulated_discount;
        }

        let accumulated_discount = use_discount ? 0 : foundUser.accumulated_discount;

        // Get voucher UUID
        let voucher_uuid_string = "";
        let voucher_uuid = Buffer.alloc(16);
        voucher_uuid.writeBigUInt64LE(buffer.readBigUInt64LE(1 + 16 * sizeBasket + 16 + 1));
        voucher_uuid.writeBigUInt64LE(buffer.readBigUInt64LE(1 + 16 * sizeBasket + 16 + 1 + 8), 8);

        if (voucher_uuid.readBigInt64LE() !== 0n) {
          voucher_uuid_string = uuid({ random: [...voucher_uuid] });
          console.log(voucher_uuid_string);

          await db.Voucher.findOne({ _id: voucher_uuid_string, user: foundUser._id })
            .then(async (voucher) => {
              // Update accumulated discount and remove voucher
              accumulated_discount += 0.15 * totalPrice;
              await voucher.remove();
            })
            .catch((error) => {
              console.log(error);
              next({ status: 401, message: "Voucher not valid." });
            });
        }

        const total_accumulated = finalPrice + foundUser.total_accumulated;
        if ((total_accumulated % 100) == 0) {
          let newVoucher = await db.Voucher.create({ _id: uuid(), user: foundUser._id });
          await foundUser.vouchers.push(newVoucher._id);
        }

        let transaction = await db.ShoppingBasket.create({
          _id: uuid(),
          products: basket.map(prod => prod._id),
          user: foundUser._id,
          total_price: totalPrice,
          paid_price: finalPrice
        });

        console.log(transaction);

        await foundUser.shopping_baskets.push(transaction._id);
        await foundUser.update({ total_accumulated: total_accumulated, accumulated_discount: accumulated_discount });
        await foundUser.save();

        res.status(200).json({ totalPrice: finalPrice });
      })
      .catch((error) => {
        console.log(error);
        next(error);
      });

  } catch (error) {
    console.log(error);
    next(error);
  }
};

exports.listBaskets = async function (req, res, next) {
  try {
    let buffer = Buffer.from(req.query.user_id, "base64");
    let content = buffer.slice(0, 16);
    let signature = buffer.slice(16, buffer.length);
    const uuid_user_string = uuid({ random: [...content] });


    await db.User.findOne({ _id: uuid_user_string })
      .then(async (foundUser) => {
        let result = validateSignature(content, signature, foundUser);

        if (!result) {
          next({ status: 401, message: "Signature invalid." })
        }

        let shopping_baskets = await db.ShoppingBasket.find({ user: foundUser._id }).populate('products');
        if (shopping_baskets.length !== 0) {
          shopping_baskets = shopping_baskets.map(sb => ({ "_id": sb._id, "products": getProducts(sb.products), "createdAt": sb.createdAt, "total_price": sb.total_price, "paid_price": sb.paid_price }));
        }
        res.status(200).json({ "transactions": shopping_baskets });

      })
      .catch((err) => next(err));
  } catch (error) {
    next(error);
  }
};

exports.listVouchers = async function (req, res, next) {
  try {
    let buffer = Buffer.from(req.query.user_id, "base64");
    let content = buffer.slice(0, 16);
    let signature = buffer.slice(16, buffer.length);
    const uuid_user_string = uuid({ random: [...content] });


    await db.User.findOne({ _id: uuid_user_string })
      .then(async (foundUser) => {
        let result = validateSignature(content, signature, foundUser);

        if (!result) {
          next({ status: 401, message: "Signature invalid." })
        }
        let vouchers = await db.Voucher.find({ user: foundUser._id }, '_id');
        vouchers = vouchers.map(v => ({ "_id": v._id }));
        res.status(200).json({ "vouchers": vouchers });
      })
      .catch((err) => next(err));
  } catch (error) {
    next(error);
  }
}

exports.getDiscount = async function (req, res, next) {
  try {
    let buffer = Buffer.from(req.query.user_id, "base64");
    let content = buffer.slice(0, 16);
    let signature = buffer.slice(16, buffer.length);
    const uuid_user_string = uuid({ random: [...content] });
    console.log(uuid_user_string);

    await db.User.findOne({ _id: uuid_user_string })
      .then((foundUser) => {
        let result = validateSignature(content, signature, foundUser);

        if (!result) {
          next({ status: 401, message: "Signature invalid." })
        }

        res.status(200).json({ "discount": foundUser.accumulated_discount });
      })
      .catch((err) => next(err));

  } catch (error) {
    next(error);
  }
}

function validateSignature(content, signature, user) {
  const publicKey = user.public_key.replace(/\n/g, '');
  const decryptionKey = new nodeRSA();
  decryptionKey.importKey(publicKey, 'public');
  decryptionKey.setOptions({ signingScheme: 'pkcs1-sha256' });
  return decryptionKey.verify(content, signature);
}


function getProducts(products) {
  return products.map(p => ({ "_id": p._id, "name": p.name, "price": p.price }));
}