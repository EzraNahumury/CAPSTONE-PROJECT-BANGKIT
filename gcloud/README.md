# Dokumentasi APIs TrashUp
## Endpoint
<blockquote style="color: white;">
    https://backend-fix-dot-capstone-441912.de.r.appspot.com
</blockquote>

## Headers
<blockquote style="color: white;">
    api-key
</blockquote>

### Admin Get All Users
- URL
  - <span style="color: white;">/admin/users</span>
- Method
  - GET
- Parameters
  - <span style="color: white;">adminKey</span> as string
- Response
```
{
    "statuscode": 200,
    "message": "Success get all data from userdata",
    "datas": [
        {
            "id": 1,
            "name_user": "thanel",
            "email_user": "thanel@gmail.com",
            "password_user": "12345678",
            "createdAt": "2024-11-28T05:46:44.000Z",
            "updatedAt": "2024-11-28T05:46:44.000Z"
        },
    ]
}
```
### Register
- URL
  - /auth/register
- Method
  - POST
- Request Body
  - name_user as string <i>(Field ini mewakili nama pengguna. Wajib diisi.)</i>
  - email_user as string, must be unique. <i>Field ini mewakili email pengguna. Email harus unik dan wajib diisi. Jika email sudah ada di database, maka akan memunculkan error Email already exists.</i>
    ```
    {
        "error": true,
        "message": "Email already exists."
    }
    ```
  - password_user as string, must be at least 8 characters. <i>Field ini mewakili kata sandi pengguna. Wajib diisi dan harus memiliki minimal 8 karakter. Jika kurang dari 8 karakter, maka akan menghasilkan error Invalid input. Password must be at least 8 characters.</i>
    ```
        {
            "error": true,
            "message": "Invalid input. Password must be at least 8 characters."
        }
    ```
- Response Body
```
{
    "error": false,
    "message": "User Created"
}
```
### Login
- URL
  - /auth/login
- Method
  - POST
- Request Body
  - <span style="color:white;">email_user as string, must be unique.</span> <i>Field ini mewakili email pengguna yang digunakan untuk login. Wajib diisi dan harus sesuai dengan email yang telah terdaftar di database.</i>
  - <span style="color:white;">password_user as string, must be at least 8 characters.</span> <i>Field ini mewakili kata sandi pengguna. Wajib diisi dan harus cocok dengan kata sandi yang terdaftar di database.</i>
- Response Body
```
{
    "error": false,
    "message": "success",
    "loginResult": {
        "userId": 16,
        "name": "brilian",
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjE2LCJpYXQiOjE3MzI4ODY1ODgsImV4cCI6MTczMjk3Mjk4OH0.91mcYVVxZdiS3EdTvckMbyuwqHQvlr70Ssp-zffHUE8"
    }
}
```

### Get All Tutorials
- URL
  - <span style="color: white;">/tutorials</span>
- Method
  - GET
- Response
```
    {
    "payload": {
            "status_code": 200,
            "message": "success get all data from tutorial",
            "datas": [
                {
                    "id": 3,
                    "title": "Pot Tanaman dari Botol Plastik",
                    "desc": "Botol plastik bekas dapat dimanfaatkan kembali sebagai pot bunga lucu bergambar hewan. Adapun cara membuatnya, yaitu",
                    "tools": "Botol 2 liter-Pewarna atau cat-Pisau",
                    "steps": "Potong sepertiga bagian bawah botol.-Pada salah satu sisi, potong botol menjadi dua bentuk segitiga sehingga terlihat seperti telinga.-Cat botol dengan warna yang diinginkan.-Gambar wajah hewan pada botol, misalnya wajah kucing atau anjing.-Isi botol dengan tanah dan tanaman yang ingin ditanam.",
                    "pitcURL": "https://storage.googleapis.com/foto-tutorial/Pot%20Tanaman%20dari%20Botol%20Plastik.jpg",
                    "wasteType": "Botol",
                    "wasteGroup": "Plastik",
                    "totalView": 1
                }
            ]
        }
    }
```
### Get Spesific Tutorials
- URL
  - <span style="color: white;">/plastik, /kardus, /perintilan, /kaca, /besi, /kertas</span>
- Method
  - GET
- Response
```
    {
    "payload": {
            "status_code": 200,
            "message": "success get all data from tutorial",
            "datas": [
                {
                    "id": 3,
                    "title": "Pot Tanaman dari Botol Plastik",
                    "desc": "Botol plastik bekas dapat dimanfaatkan kembali sebagai pot bunga lucu bergambar hewan. Adapun cara membuatnya, yaitu",
                    "tools": "Botol 2 liter-Pewarna atau cat-Pisau",
                    "steps": "Potong sepertiga bagian bawah botol.-Pada salah satu sisi, potong botol menjadi dua bentuk segitiga sehingga terlihat seperti telinga.-Cat botol dengan warna yang diinginkan.-Gambar wajah hewan pada botol, misalnya wajah kucing atau anjing.-Isi botol dengan tanah dan tanaman yang ingin ditanam.",
                    "pitcURL": "https://storage.googleapis.com/foto-tutorial/Pot%20Tanaman%20dari%20Botol%20Plastik.jpg",
                    "wasteType": "Botol",
                    "wasteGroup": "Plastik",
                    "totalView": 1
                }
            ]
        }
    }
```

### Update Tutorial View
- URL
  - <span style="color: white;">/views</span>
- Method
  - POST
- Request Body
  - id as integer
  - jenisSampah as string
- Response
```
{
    "payload": {
        "status_code": 201,
        "message": "success update view",
        "datas": {
            "fieldCount": 0,
            "affectedRows": 1,
            "insertId": 0,
            "info": "Rows matched: 1  Changed: 1  Warnings: 0",
            "serverStatus": 2,
            "warningStatus": 0,
            "changedRows": 1
        }
    }
}
```