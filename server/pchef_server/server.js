// Required Libraries
var express = require('express');
var mysql = require('mysql');
var https = require('https');
const fs = require('fs');
var jsonDiff = require('json-diff');
var async = require('async');
const dgram = require("dgram");
// Certification for https
var privateKey = fs.readFileSync( 'privatekey.pem' );
var certificate = fs.readFileSync( 'server.crt' );
// Debugging purposes
var DEBUG = false;
var LOGS = false;


// Create mysql connection with parameters 
var sqlCon = mysql.createConnection({
    host:'localhost',
    user:'root',
    password:'12345', //not real password
    database:'DB_Recipe_Remote'
});
// Initialize express TCP server with REST API 
var app = express();
app.use("/", express.static("../public"));
app.use(express.json());
app.use(express.urlencoded({extended: true}));

////////////////////////////////// POST commands ///////////////////////////////////////////

/* POST request: /upload: recieves JSON Array of new recipe from phone and returns a 
status code for confirmation */
app.post("/upload", (req, res) => {
  try { var myItemsJSON = JSON.parse(req.body.upload); } catch(e) { var myItemsJSON = req.body; }
  var query = "INSERT INTO RECIPE (RName, Description, Instructions) VALUES('"+myItemsJSON.RecipeName+"', '"+myItemsJSON.Description+"', '"+myItemsJSON.Instructions+"');";
  if(LOGS) console.log("------------------UPLOAD------------------")
  if(LOGS) console.log(myItemsJSON)
  sqlCon.query(query,function(error,rows,fields){
    sqlCon.on('error',function(err){
        console.log('[MYSQL]ERROR',err);
    });

    if(rows && rows.length) {
      res.sendStatus(200); // -> positive response
    }

  });
});

/* POST request: /possible: recieves JSON Array of ingredients from phone and returns a 
JSON Array of possible recipes that can be prepared based on the remote DB */ 
app.post("/possible", (req, res) => {
  try { var myItemsJSON = JSON.parse(req.body.possible); } catch(e) { var myItemsJSON = req.body; }
  if(LOGS) console.log("------------------GENERATE------------------")
  if(LOGS) console.log(myItemsJSON)
  var query = "select * from RECIPE;"; // -> initial query to get all recipes from database       
  var possibleRecipes = []; // -> initialize an array for storing recipes 
  var rowsCounter = 1; // -> this keeps track the ID of the recipe
  // Inner mysql query for each row (recipe) in the database
  sqlCon.query(query,function(error,rows,fields){
      sqlCon.on('error',function(err){
          console.log('[MYSQL]ERROR',err);
      });
      // async operation to traverse the database
      async.each(rows, function (row, callback) {
          // query to get ingredients with amounts and measures
          var ingredientQuery = "SELECT ri.Amount AS 'Amount', mu.Measure AS 'Measure', i.FName AS 'Ingredient' "
            +"FROM RECIPE r JOIN REC_INGREDIENT ri on r.id = ri.RIID JOIN FOOD i on i.FID = ri.Food_ID "
            +"LEFT OUTER JOIN MEASURE mu on mu.MID = Measure_ID where r.id = ?";
          sqlCon.query(ingredientQuery, row.id,function(food_err, ingredients, ingredient_fields) {
              if (food_err) callback(food_err);

              if(ingredients && ingredients.length) {
                  // parsing the results to just get the ingredients for comparison purposes
                  var temp = [];
                  async.forEach(ingredients, function (ing) {
                      temp.push({"Ingredient":ing.Ingredient});
                  });
                  
                  // sorting arrays for comparison
                  temp.sort((a,b) => (a.Ingredient > b.Ingredient) ? 1 : ((b.Ingredient > a.Ingredient) ? -1 : 0))
                  myItemsJSON.sort((a,b) => (a.Ingredient > b.Ingredient) ? 1 : ((b.Ingredient > a.Ingredient) ? -1 : 0))
                  // this is the sorted ingredients from our database 
                  DBList = JSON.parse(JSON.stringify(temp));
                  // using JASON DIFF to compare ingredients
                  var diff = jsonDiff.diffString(DBList,myItemsJSON);
                  if(DEBUG) console.log(diff); // -> Debugging purposes
                  if(diff.includes("-")) {
                      //ToDo: by the amount of '-', we can suggest to buy certain items to be able to cook the recipes
                  }
                  else { // We found a recipe we can prepare with our ingredients
                      var suggestedRecipe = JSON.parse(JSON.stringify(row)); // -> get suggested recipe
                      suggestedRecipe["Ingredients"] = JSON.parse(JSON.stringify(ingredients)); // -> add the ingredients
                      possibleRecipes.push(suggestedRecipe); // -> added to our array of possible recipes 
                  }
                  // If we are at the end of the recipe database, we send the results to the phone
                  if(rowsCounter == rows.length) {
                      if(DEBUG) console.log(JSON.stringify(possibleRecipes)); // -> Debugging purposes
                      
                      if(possibleRecipes.length == 0) {
                        res.status(500).send({ error: "no recipes available" }); // -> negative response
                      }
                      else {
                        res.end(JSON.stringify(possibleRecipes)); // -> positive response to the phone
                      }  
                  }
                  rowsCounter++; // increase row count
                    
              }
              else {
                  if(DEBUG) console.log('no recipes available'); // -> Debugging purposes 
                  res.status(500).send({ error: "no recipes available" }); // -> negative response
              }

              callback();
          });

      });
      //sqlCon.end();
  });
});

////////////////////////////////// UDP Server ///////////////////////////////////////////

// The idea is to have fast check to see if server is alive to notify the phone
const UDPserver = dgram.createSocket(
    {
      type: "udp4",
      reuseAddr: true // reuse port 3000
    },
    (buffer, sender) => {
      const message = buffer.toString();
      console.log({ // -> log connection
        kind: "UDP_MESSAGE",
        message,
        sender
      });
  
      // respond to sender
      UDPserver.send(message.toUpperCase(), sender.port, sender.address, error => {
        if (error) {
          console.error(error);
        } else {
          console.log({ // -> log response
            kind: "RESPOND",
            message: "Yes, alive and kicking",//message.toUpperCase(),
            sender
          });
        }
      });
    }
  );

UDPserver.bind(3000); // -> Binds socket to port
console.log("UDP Server Listening on Port 3000"); // -> log listening message

////////////////////////////////// TCP Server ///////////////////////////////////////////
// Starts https server with certificates
https.createServer({
    key: privateKey,
    cert: certificate
}, app).listen(3000,'0.0.0.0'); // -> listens to localhost 
console.log("TCP Server Listening on Port 3000"); // -> log listening message

module.exports = app;
