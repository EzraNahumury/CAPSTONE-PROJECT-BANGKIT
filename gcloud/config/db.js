const { Sequelize, DataTypes } = require('sequelize');
require('dotenv').config();

const sequelize = new Sequelize(process.env.DB_NAME, process.env.DB_USER, process.env.DB_PASS, {
  host: process.env.DB_HOST,
  dialect: 'mysql', // atau 'postgres', 'sqlite', dll.
});

const db = sequelize.define('userdata', {
  name_user: {
    type: DataTypes.STRING, 
    allowNull: false,
  },
  email_user: {
    type: DataTypes.STRING,
    allowNull: false,
    unique: true,
  },
  password_user: {
    type: DataTypes.STRING,
    allowNull: false,
  },
});

module.exports = db