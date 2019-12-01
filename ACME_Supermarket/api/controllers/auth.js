const db = require('../models');

exports.signup = async function (req, res, next) {
  try {

    console.log(req.body);
    // req.body contains username, name, email, password and user public key
    let user_info = { username: req.body.username, name: req.body.name, email: req.body.email, public_key: req.body.user_public_key, accumulated_discount: 0, total_accumulated: 0 };
    let user = await db.User.create(user_info);

    let card_info = {
      name: req.body.card_name, number: req.body.card_number, valid_date_month: req.body.card_month,
      valid_date_year: req.body.card_year, cvc: req.body.card_cvc, user: user._id
    }
    let card = await db.PaymentCard.create(card_info);
    user.payment_card = card._id;
    user.save();

    let { _id } = user;

    return res.status(200).json({
      _id,
      supermarket_public_key: process.env.SUPERMARKET_PUBLIC_KEY
    });

  } catch (err) {
    if (err.code === 11000) {
      err.message = 'Sorry, that username and/or email is taken';
    }
    return next({
      status: 400,
      message: err.message
    });
  }
};
