const db = require('../models');

/**
 * Verifies if basket and request is valid, checks if payment is successful and creates a basket and saves it to user.
 * Updates user total accumulated.
 */
exports.checkoutBasket = async function(req, res, next) {
  try {
    res.status(200).json('Checkout Basket Route in the works...');
  } catch (error) {
    next(error);
  }
};

exports.listBaskets = async function(req, res, next) {
  try {
    res.status(200).json('List Baskets Route in the works...');
  } catch (error) {
    next(error);
  }
};
