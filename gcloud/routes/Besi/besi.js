const express = require('express');
const bodyParser = require('body-parser')
const dataBase = require("../../config/connection")
const responseFormat = require("../response")
const besi = express.Router();

besi.get('/', (req, res) => {
    const query = 'SELECT tutorial.idTutorial as "id", tutorial.judul as "title", tutorial.deskripsi as "desc", detailtutorial.alat as "tools", detailtutorial.langkahKerja as "steps", detailtutorial.gambar as "pitcURL", detailtutorial.tipeSampah as "wasteType", detailtutorial.jenisSampah as "wasteGroup", detailtutorial.totalView FROM tutorial LEFT join detailtutorial ON tutorial.idTutorial = detailtutorial.idTutorial WHERE detailtutorial.jenisSampah = "Besi/aluminium" ORDER BY detailtutorial.totalView DESC'
    dataBase.query(query, (error, result) => {
        if (error) {
            responseFormat(500, null, "error get data from database besi", res)
        } else {
            responseFormat(200, result, "success get data from database", res)
        }
    })
})

module.exports = besi;