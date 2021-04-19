/* eslint-disable max-len */
const functions = require("firebase-functions");

exports.checkForAllFields = functions.database
    .ref("recipeBook/{recipeId}")
    .onCreate((snapshot, context)=>{
      return snapshot.ref.update({status: 0});
    });


exports.date = functions.database
    .ref("TestVal/")
    .onCreate((snapshot, context)=>{
      let name = "recipes/1c080261-11e0-4fab-92a1-7844bf4c335a.jpg";
      name = name.replace("recipes/", "");
      name = name.replace(".jpg", "");
      console.log(name);
    });


exports.formatFood = functions.database
    .ref("/food/{foodItem}/foodNutrients/")
    .onWrite((change )=> {
      const list = ["Total fat (NLEA)", "Cholesterol", "Protein", "Carbohydrate, by difference", "Sodium, Na", "Energy (Atwater Specific Factors)"];
      return change.after.forEach((child)=>{
        if (list.indexOf(child.key) <= -1) {
          change.after.ref.child(child.key).set(null);
        }
      });
    });
