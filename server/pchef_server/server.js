var express = require('express');
var mysql = require('mysql');
var https = require('https');
const fs = require('fs');
var jsonDiff = require('json-diff');
var async = require('async');

var privateKey = fs.readFileSync( '../../../privatekey.pem' );
var certificate = fs.readFileSync( '../../../server.crt' );

// Switch to default password 
var sqlCon = mysql.createConnection({
    host:'localhost',
    user:'root',
    password:'12345', //not real password
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

app.post("/possible",(req,res,next)=>{
    var post_data = req.body;
    var data = post_data.search;
    
});


//////////////////////////////////////////////////////////////////////////////////////////

function test(data) {
    var myItemsJSON = JSON.parse(data);
    var query = "select * from RECIPE;";        
    var possibleRecipes = [];
    var rowsCounter = 1;
    sqlCon.query(query,function(error,rows,fields){
        if (error) throw err;

        async.each(rows, function (row, callback) {
            var ingredientQuery = "SELECT ri.Amount AS 'Amount', mu.Measure AS 'Measure', i.FName AS 'Ingredient' FROM RECIPE r JOIN REC_INGREDIENT ri on r.id = ri.RIID JOIN FOOD i on i.FID = ri.Food_ID LEFT OUTER JOIN MEASURE mu on mu.MID = Measure_ID where r.id = ?";

            sqlCon.query(ingredientQuery, row.id,function(food_err, ingredients, ingredient_fields) {
                if (food_err) callback(food_err);

                if(ingredients && ingredients.length) {
                    var temp = [];
                    async.forEach(ingredients, function (ing) {
                        temp.push({"Ingredient":ing.Ingredient});
                    });
                    
                    // sorting arrays for comparison
                    temp.sort((a,b) => (a.Ingredient > b.Ingredient) ? 1 : ((b.Ingredient > a.Ingredient) ? -1 : 0))
                    DBList = JSON.parse(JSON.stringify(temp));
                    myItemsJSON.sort((a,b) => (a.Ingredient > b.Ingredient) ? 1 : ((b.Ingredient > a.Ingredient) ? -1 : 0))

                    var diff = jsonDiff.diffString(DBList,myItemsJSON);
                    //console.log(diff);
                    if(diff.includes("-")) {
                        //ToDo: by the amount of '-', we can suggest to buy certain items to be able to cook the recipes
                    }
                    else {
                        var suggestedRecipe = JSON.parse(JSON.stringify(row));
                        suggestedRecipe["Ingredients"] = JSON.parse(JSON.stringify(ingredients));
                        possibleRecipes.push(suggestedRecipe);
                    }

                    if(rowsCounter == rows.length) {
                        console.log(JSON.stringify(possibleRecipes));
                        // res.end(JSON.stringify(possibleRecipes));
                    }
                    rowsCounter++;
                     
                }
                else {
                    console.log('no recipes available');
                }

                callback();
            });

        });
        sqlCon.end();
    });    
}


test('[{"Ingredient":"sugar"},{"Ingredient":"egg"}]');  

/////////////////////////////////////////////////////////////////////////////////////////

https.createServer({
    key: privateKey,
    cert: certificate
}, app).listen(3000,'0.0.0.0');

console.log("Server Listening on Port 3000");