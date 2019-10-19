const db = require('../models');

exports.signin = async function(req, res, next) {
  try {
    let user = await db.User.findOne({
      username: req.body.username
    });

    let isMatch = await user.comparePassword(req.body.password);
    let { _id, username } = user;

    if (isMatch) {
      return res.status(200).json({
        _id,
        username,
        name,
        public_key: process.env.SUPERMARKET_PUBLIC_KEY
      });
    } else {
      return next({
        status: 400,
        message: 'Invalid Username/Password.'
      });
    }
  } catch (err) {
    return next({
      status: 400,
      message: 'Invalid Username/Password.'
    });
  }
};

exports.signup = async function(req, res, next) {
  try {
    // req.body contains username, name, email, password and user public key
    let user_info = { username: req.body.username, name: req.body.name, email: req.body.email, password: req.body.password, public_key: req.body.public_key}
    let user = await db.User.create(user_info);
    let { _id, username, name } = user;

    return res.status(200).json({
      _id,
      username,
      name,
      public_key: process.env.SUPERMARKET_PUBLIC_KEY
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
