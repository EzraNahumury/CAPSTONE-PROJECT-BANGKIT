require('@google-cloud/debug-agent').start()
require('dotenv').config();

const express = require('express')
const app = express()
// const port = 8000
const bodyParser = require('body-parser')
const dataBase = require("../config/connection")

const responseFormat = require("./response")

const login = require("../controller/userController")

const apiKey = process.env.API_KEY

// Middleware untuk validasi API Key
app.use((req, res, next) => {
    const userApiKey = req.headers['api-key'];
    if (userApiKey && userApiKey === apiKey) {
        next(); // Lanjutkan request jika API key valid
    } else {
        res.status(403).json({ message: 'Forbidden: Invalid API Key' });
    }
});

app.use("/auth", login)

const plastik = require("./plastik/plastik")
app.use("/plastik", plastik)

const kaca = require("./Kaca/kaca");
app.use("/kaca", kaca);

const besi = require("./Besi/besi");
app.use("/besi", besi);

const kertas = require("./Kertas/kertas");
app.use("/kertas", kertas);

const kardus = require("./Kardus/kardus");
app.use("/kardus", kardus);

const perintilan = require("./Perintilan/perintilan");
app.use("/perintilan", perintilan);

const admin = require("../controller/adminController");
app.use("/admin", admin);


// cara menggunakan bodyParser
app.use(bodyParser.json()) // artinya akan mengubah kiriman request dari depan ke bentuk json

app.get("/", (req, res) => {
    res.json(
        {
            message: "API is running"
        }
    ).status(200)
})

app.get("/tutorials", (req, res) => {
    const query = 'SELECT tutorial.idTutorial as "id", tutorial.judul as "title", tutorial.deskripsi as "desc", detailtutorial.alat as "tools", detailtutorial.langkahKerja as "steps", detailtutorial.gambar as "pitcURL", detailtutorial.tipeSampah as "wasteType", detailtutorial.jenisSampah as "wasteGroup", detailtutorial.totalView FROM tutorial LEFT join detailtutorial ON tutorial.idTutorial = detailtutorial.idTutorial ORDER BY detailtutorial.totalView DESC'
    dataBase.query(query, (error, result) => {
        if (error) {
            responseFormat(500, null, "error get all data from tutorial", res)
        } else {
            responseFormat(200, result, "success get all data from tutorial", res)
        }
    })
})

app.post("/views", (req, res) => {
    const { id, jenisSampah } = req.body
    const query = 'UPDATE detailtutorial SET detailtutorial.totalView = detailtutorial.totalView + 1 WHERE detailtutorial.idTutorial = ? and detailtutorial.jenisSampah = ?'
    dataBase.query(query, [id, jenisSampah], (error, result) => {
        if (error) {
            responseFormat(500, null, "error update view", res)
        } else {
            responseFormat(201, result, "success update view", res)
        }
    })
})

const PORT = process.env.PORT || 8000
app.listen(PORT, () => {
    console.log("Server is up and listening on " + PORT)
})