# ACME Electronic Supermarket

[Pratical Assignement Instructions](https://paginas.fe.up.pt/~apm/CM/docs/lab2019_1.pdf)

## API

### Setup

- `cd api/`
- `npm install`

### Run

- Dev Mode: `npm run dev`
- Normal Mode: `npm run start`

#### Dependencies

- **MongoDB**
  - Windows: https://docs.mongodb.com/manual/tutorial/install-mongodb-on-windows/
  - Linux: https://docs.mongodb.com/manual/administration/install-on-linux/
    - Arch: Install **mongodb-bin** from _AUR_
  - Mac: https://docs.mongodb.com/manual/tutorial/install-mongodb-on-os-x/

  - To run, just do: ``` mongod ``` in a terminal

#### Tools 
- **NGrok**
  - Run `ngrok http 8080`
  - Copy the address (ex: http://319c5d33.ngrok.io) to the RegisterOperation.java in the *Mobile* directory in the module **operations**.

##### Notes:
If there is any error with mongoose-uuid2, change in file *api/node_modules/mongoose-uuid2/node_modules/bson/lib/bson/objectid.js* the line **const hostname = require('os').hostname;** to **const hostname = require('os').hostname();**;
