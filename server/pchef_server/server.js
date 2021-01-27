var express = require('express');
var mysql = require('mysql');
var http = require('http');
var https = require('https');
const tls = require('tls');
const fs = require('fs');
const { Console } = require('console');

var privateKey = fs.readFileSync( '../../../privatekey.pem' );
var certificate = fs.readFileSync( '../../../server.crt' );

var sqlCon = mysql.createConnection({
    host:'localhost',
    user:'root',
    password:'12345',
    database:'DB_Recipe_Remote'
});

var app = express();
app.use("/", express.static("../public"));
app.use(express.json());
app.use(express.urlencoded({extended: true}));

app.get("/recipes",(req,res,next)=>{
    
    var query = "SELECT r.RName AS 'Recipe',r.Instructions, ri.Amount AS 'Amount', mu.Measure AS 'Measure', i.FName AS 'Ingredient' FROM RECIPE r JOIN REC_INGREDIENT ri on r.id = ri.RIID JOIN FOOD i on i.FID = ri.Food_ID LEFT OUTER JOIN MEASURE mu on mu.MID = Measure_ID";

    sqlCon.query(query,function(error,result,fields){
        sqlCon.on('error',function(err){
            console.log('[MYSQL]ERROR',err);
        });
        if(result && result.length) {
            res.end(JSON.stringify(result));
        }
        else {
            res.end(JSON.stringify('no recipes available'));
        }
    });
});

app.post("/search",(req,res,next)=>{
    var post_data = req.body;
    var recipe_search = post_data.search;

    var query = "SELECT r.RName AS 'Recipe',r.Instructions, ri.Amount AS 'Amount', mu.Measure AS 'Measure', i.FName AS 'Ingredient' FROM RECIPE r JOIN REC_INGREDIENT ri on r.id = ri.RIID JOIN FOOD i on i.FID = ri.Food_ID LEFT OUTER JOIN MEASURE mu on mu.MID = Measure_ID where r.RName = ?";

    sqlCon.query(query,[recipe_search],function(error,result,fields){
        sqlCon.on('error',function(err){
            console.log('[MYSQL]ERROR',err);
        });
        if(result && result.length) {
            res.end(JSON.stringify(result));
        }
        else {
            res.end(JSON.stringify('no recipes available'));
        }
    });
});



https.createServer({
    key: privateKey,
    cert: certificate
}, app).listen(3000,'0.0.0.0');

console.log("Server Listening on Port 3000");
