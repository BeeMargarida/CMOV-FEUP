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
    let user = await db.User.create(req.body);
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
