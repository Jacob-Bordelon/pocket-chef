var express = require('express');
var mysql = require('mysql');

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
    
    var query = "SELECT r.RName AS 'Recipe',r.Instructions, ri.Amount AS 'Amount', mu.Measure AS 'Unit of Measure', i.FName AS 'Ingredient' FROM RECIPE r JOIN REC_INGREDIENT ri on r.id = ri.RIID JOIN FOOD i on i.FID = ri.Food_ID LEFT OUTER JOIN MEASURE mu on mu.MID = Measure_ID";

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

    var query = "select * from RECIPE where RName = ?";

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



app.listen(3000,()=>{
    console.log("Running REST API on port 3000")
})