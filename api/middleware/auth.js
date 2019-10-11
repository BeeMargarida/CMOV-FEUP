exports.loginRequired = function(req, res, next) {
  try {
    return next();

    //TODO: using the user UUID in the request, check if valid user

    //TODO: if user exists, append to request the user PUBLIC KEY
  } catch (err) {
    return next({
      status: 401,
      message: 'Please log in first.'
    });
  }
};

exports.ensureCorrectUser = function(req, res, next) {
  try {
    return next();

    //TODO: Using the public key, use crypto to decrypt the message using the public key.

    //TODO: If valid decryption, req.next(), else status: 401
  } catch (err) {
    return next({
      status: 401,
      message: 'Unauthorized'
    });
  }
};
