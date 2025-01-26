require('dotenv').config();
const express = require("express");
const dataBase = require("../config/connection");
const adminRouter = express.Router();
// Middleware for admin authentication using a query parameter
const adminAuth = (req, res, next) => {
    const adminKey = req.query.adminKey; // Use query parameter for admin authentication
    const validAdminKey = process.env.ADMIN_KEY; // Define a secret key for admin

    if (!adminKey || adminKey !== validAdminKey) {
        return res.status(401).json({
            error: true,
            message: "Unauthorized access. Valid admin key is required.",
        });
    }
    next();
};

// Endpoint to get all user data
adminRouter.get("/users", adminAuth, async (req, res) => {
    const query = "SELECT * FROM userdata"
    dataBase.query(query, (error, result) => {
        if (error) {
            console.log(error)
            res.status(500).json(
                payload = {
                    statuscode: 500,
                    message: "Internal Server Error",
                    data: null
                }
            )
        } else {
            res.status(200).json(
                payload = {
                    statuscode: 200,
                    message: "Success get all data from userdata",
                    datas: result
                }
            )
        }
    })
});

module.exports = adminRouter;
