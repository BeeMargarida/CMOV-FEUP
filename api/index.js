const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const nodeRSA = require('node-rsa');
const errorHandler = require('./controllers/error');
const authRoutes = require('./routes/auth');
const supermarketRoutes = require('./routes/supermarket');
const { loginRequired, ensureCorrectUser } = require('./middleware/auth');

const PORT = 8081;
const app = express();

// TODO: Get supermarket public key
const key = new nodeRSA({b: 512});
process.env.SUPERMARKET_PUBLIC_KEY = key.exportKey('public');
process.env.SUPERMARKET_PRIVATE_KEY = key.exportKey('private');

app.use(cors());
app.use(bodyParser.json());

app.use('/auth', authRoutes);
app.use('/supermarket', loginRequired, ensureCorrectUser, supermarketRoutes);

//TODO: Add User routes
//TODO: Add Payment Card routes ? -> check if needed

app.use('/', (req, res, next) => {
  res.send('Welcome to ACME Electronic Supermarket!');
});

app.use(function(req, res, next) {
  let err = new Error('Not Found');
  err.status = 404;
  next(err);
});

app.use(errorHandler);

app.listen(PORT, function() {
  console.log(`Server is starting on port ${PORT}`);
});
