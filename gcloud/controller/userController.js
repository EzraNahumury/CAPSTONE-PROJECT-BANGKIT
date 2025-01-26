const express = require("express");
const bcrypt = require("bcrypt");
const jwt = require("jsonwebtoken");
const userRouter = express.Router();
const User = require("../config/db");

userRouter.use(express.json());

userRouter.post("/register", async (req, res) => {
    console.log(req)
    console.log(req.body)
    try {
        const { name_user, email_user, password_user } = req.body;
        if (!name_user || !email_user || !password_user || password_user.length < 8) {
            return res.status(400).json({
                error: true,
                message: "Invalid input. Password must be at least 8 characters.",
            });
        }
        // Simpan ke database
        await User.create({ name_user, email_user, password_user: password_user });

        res.status(201).json({
            error: false,
            message: "User Created",
        });
    } catch (err) {
        console.log(err);
        if (err.name === "SequelizeUniqueConstraintError") {
            res.status(400).json({
                error: true,
                message: "Email already exists.",
            });
        } else {
            res.status(500).json({
                error: true,
                message: "Failed to create user.",
            });
        }
    }
});

userRouter.post("/login", async (req, res) => {
    console.log(req)
    console.log(req.body)
    try {
        // console.log(req.body);
        const { email_user, password_user } = req.body;

        // Cari user berdasarkan email
        const user = await User.findOne({ where: { email_user } });
        if (!user) {
            return res.status(404).json({
                error: true,
                message: "User not found",
            });
        }

        // Cek password
        bcrypt.hash(password_user, 10, (err, encryptPass) => {
            if (err) {
                console.error("Error hashing string a:", err);
                return;
            }

            // Cocokkan b dengan hash dari a
            bcrypt.compare(user.password_user, encryptPass, (err, isMatch) => {
                if (err) {
                    console.error("Error comparing strings:", err);
                    return;
                }

                if (isMatch) {
                    // Buat token JWT
                    const token = jwt.sign(
                        { userId: user.id, name: user.name },
                        "your-secret-key",
                        { expiresIn: "1d" }
                    );

                    res.status(200).json({
                        error: false,
                        message: "success",
                        loginResult: {
                            userId: user.id,
                            name: user.name_user,
                            token,
                        },
                    });
                } else {
                    return res.status(401).json({
                        error: true,
                        message: "Invalid password",
                    });
                }
            });
        });
    } catch (err) {
        res.status(500).json({
            error: true,
            message: "Login failed",
        });
    }
});


module.exports = userRouter;