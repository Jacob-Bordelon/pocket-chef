/* eslint-disable max-len */
const functions = require("firebase-functions");

const arr = [
  "author",
  "description",
  "ingredients",
  "instructions",
  "prep_time",
  "cook_time",
  "difficulty",
];


exports.checkForAllFields = functions.database
    .ref("recipeBook/{recipeId}")
    .onCreate((snapshot, context)=>{
      const hasAllKeys = arr.every((item) => (snapshot.hasChild(item)));
      let img = "";
      const id = Date.now().toString();
      if (snapshot.child("image").exists()) {
        img = snapshot.child("image").val();
      }
      return snapshot.ref
          .transaction((t)=>{
            if (!hasAllKeys) {
              console.log("Is missing an item");
              return;
            } else {
              return {
                author: snapshot.child("author").val(),
                description: snapshot.child("description").val(),
                difficulty: snapshot.child("difficulty").val(),
                prep_time: snapshot.child("prep_time").val(),
                cook_time: snapshot.child("cook_time").val(),
                ingredients: snapshot.child("ingredients").val(),
                instructions: snapshot.child("instructions").val(),
                serving_size: snapshot.child("serving_size").val(),
                status: 0,
                id: id,
                image: img,
                rating: 0,
              };
            }
          })
          .then((r) => {
            return {response: "ok"};
          })
          .catch((error) => {
            console.log(error);
          });
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
