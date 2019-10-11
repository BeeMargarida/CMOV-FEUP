const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const errorHandler = require('./controllers/error');
const db = require('./models');
const authRoutes = require('./routes/auth');
const supermarketRoutes = require('./routes/supermarket');
const { loginRequired, ensureCorrectUser } = require('./middleware/auth');

const PORT = 8081;
const app = express();

// TODO: Get supermarket public key
process.env.SUPERMARKET_PUBLIC_KEY = '5124';

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
