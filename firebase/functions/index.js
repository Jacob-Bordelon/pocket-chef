const functions = require("firebase-functions");

const arr = ["author","description","ingredients","instructions","prep_time","cook_time","difficulty", "serving_size"];


exports.checkForAllValues = functions.database
.ref("recipeBook/{recipeId}")
.onCreate((snapshot,context)=>{
    const obj = snapshot.val();
    const hasAllKeys = arr.every(item => obj.hasOwnProperty(item));
    const img = "";
    const newData_id = Date.now().toString();

    if(snapshot.child("image").exists()){
        img = snapshot.child("image");
    }



    return snapshot.ref
        .transaction(t=>{

            if(!hasAllKeys){
                console.log("Is missing an item");
                return;
            }
            else{
                return {
                    author:snapshot.child("author").val(),
                    description:snapshot.child("description").val(),
                    difficulty:snapshot.child("difficulty").val(),
                    prep_time:snapshot.child("prep_time").val(),
                    cook_time:snapshot.child("cook_time").val(),
                    ingredients:snapshot.child("ingredients").val(),
                    instructions:snapshot.child("instructions").val(),
                    serving_size:snapshot.child("serving_size").val(),
                    status:0,
                    id:newData_id,
                    image:img,
                    rating:0




                }



            }
        })
        .then(r => {
            return { response: "ok" };
        })
        .catch(error => {
            console.log(error);
            // See https://firebase.google.com/docs/functions/callable?authuser=0#handle_errors
        });

});




