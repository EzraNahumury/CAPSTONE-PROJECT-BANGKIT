require('dotenv').config();

const mysql = require('mysql2');

// Membuat koneksi ke database menggunakan mysql2
const dataBase = mysql.createConnection({
    host: process.env.DB_HOST,     // Alamat server database
    user: process.env.DB_USER,          // Username database
    password: process.env.DB_PASS,   // Password database
    database: process.env.DB_NAME,      // Nama database
});

// Tes koneksi untuk memastikan berhasil
dataBase.connect((err) => {
    if (err) {
        console.error('Koneksi database gagal:', err.message);
    } else {
        console.log('Koneksi database berhasil!');
    }
});

// Ekspor koneksi untuk digunakan di file lain
module.exports = dataBase;
